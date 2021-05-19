package com.stork.blockspam.ui.callingincome


import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract.*
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.SparseArray
import com.stork.blockspam.extension.getIntValue
import com.stork.blockspam.extension.getStringValue
import com.stork.blockspam.model.PhoneContact
import com.stork.blockspam.ui.callingincome.CallManager.Companion.ensureBackgroundThread
import com.stork.blockspam.utils.AppPermission


class SimpleContactsHelper(val context: Context) {

    fun getNameFromPhoneNumber(number: String): String {
        if (!AppPermission.hasPermission(context, AppPermission.PER_READ_CONTACTS)) {
            return number
        }

        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val projection = arrayOf(
                PhoneLookup.DISPLAY_NAME
        )

        try {
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor.use {
                if (cursor?.moveToFirst() == true) {
                    return cursor.getStringValue(PhoneLookup.DISPLAY_NAME)
                }
            }
        } catch (e: Exception) {
        }
        return number
    }

    fun getPhotoUriFromPhoneNumber(number: String): String {
        if (!AppPermission.hasPermission(context, AppPermission.PER_READ_CONTACTS)) {
            return ""
        }

        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val projection = arrayOf(
                PhoneLookup.PHOTO_URI
        )

        try {
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor.use {
                if (cursor?.moveToFirst() == true) {
                    return cursor.getStringValue(PhoneLookup.PHOTO_URI) ?: ""
                }
            }
        } catch (e: Exception) {
        }
        return ""
    }


    fun getAvailableContacts(favoritesOnly: Boolean, callback: (ArrayList<PhoneContact>) -> Unit) {
        ensureBackgroundThread {
            val names = getContactNames(favoritesOnly)
            var allContacts = getContactPhoneNumbers(favoritesOnly)
            allContacts.forEach {
                val contactId = it.rawId
                val contact = names.firstOrNull { it.rawId == contactId }
                val name = contact?.name
                if (name != null) {
                    it.name = name
                }

                val photoUri = contact?.photoUri
                if (photoUri != null) {
                    it.photoUri = photoUri
                }
            }

            allContacts = allContacts.filter { it.name.isNotEmpty() }.distinctBy {
                val startIndex = Math.max(0, it.phoneNumbers.first().length - 9)
                it.phoneNumbers.first().substring(startIndex)
            }.distinctBy { it.rawId }.toMutableList() as ArrayList<PhoneContact>

            val birthdays = getContactEvents(true)
            var size = birthdays.size()
            for (i in 0 until size) {
                val key = birthdays.keyAt(i)
                allContacts.firstOrNull { it.rawId == key }?.birthdays = birthdays.valueAt(i)
            }

            val anniversaries = getContactEvents(false)
            size = anniversaries.size()
            for (i in 0 until size) {
                val key = anniversaries.keyAt(i)
                allContacts.firstOrNull { it.rawId == key }?.anniversaries = anniversaries.valueAt(i)
            }

            allContacts.sort()
            callback(allContacts)
        }
    }

    private fun getContactNames(favoritesOnly: Boolean): List<PhoneContact> {
        val contacts = ArrayList<PhoneContact>()
        val startNameWithSurname = false
        val uri = Data.CONTENT_URI
        val projection = arrayOf(
                Data.RAW_CONTACT_ID,
                Data.CONTACT_ID,
                CommonDataKinds.StructuredName.PREFIX,
                CommonDataKinds.StructuredName.GIVEN_NAME,
                CommonDataKinds.StructuredName.MIDDLE_NAME,
                CommonDataKinds.StructuredName.FAMILY_NAME,
                CommonDataKinds.StructuredName.SUFFIX,
                CommonDataKinds.StructuredName.PHOTO_THUMBNAIL_URI,
                CommonDataKinds.Organization.COMPANY,
                CommonDataKinds.Organization.TITLE,
                Data.MIMETYPE
        )

        var selection = "(${Data.MIMETYPE} = ? OR ${Data.MIMETYPE} = ?)"

        if (favoritesOnly) {
            selection += " AND ${Data.STARRED} = 1"
        }

        val selectionArgs = arrayOf(
                CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                CommonDataKinds.Organization.CONTENT_ITEM_TYPE
        )

        context.queryCursor(uri, projection, selection, selectionArgs) { cursor ->
            val rawId = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val contactId = cursor.getIntValue(Data.CONTACT_ID)
            val mimetype = cursor.getStringValue(Data.MIMETYPE)
            val photoUri = cursor.getStringValue(CommonDataKinds.StructuredName.PHOTO_THUMBNAIL_URI) ?: ""
            val isPerson = mimetype == CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            if (isPerson) {
                val prefix = cursor.getStringValue(CommonDataKinds.StructuredName.PREFIX) ?: ""
                val firstName = cursor.getStringValue(CommonDataKinds.StructuredName.GIVEN_NAME) ?: ""
                val middleName = cursor.getStringValue(CommonDataKinds.StructuredName.MIDDLE_NAME) ?: ""
                val familyName = cursor.getStringValue(CommonDataKinds.StructuredName.FAMILY_NAME) ?: ""
                val suffix = cursor.getStringValue(CommonDataKinds.StructuredName.SUFFIX) ?: ""
                if (firstName.isNotEmpty() || middleName.isNotEmpty() || familyName.isNotEmpty()) {
                    val names = if (startNameWithSurname) {
                        arrayOf(prefix, familyName, middleName, firstName, suffix).filter { it.isNotEmpty() }
                    } else {
                        arrayOf(prefix, firstName, middleName, familyName, suffix).filter { it.isNotEmpty() }
                    }

                    val fullName = TextUtils.join(" ", names)
                    val contact = PhoneContact(rawId, contactId, fullName, photoUri, ArrayList(), ArrayList(), ArrayList())
                    contacts.add(contact)
                }
            }

            val isOrganization = mimetype == CommonDataKinds.Organization.CONTENT_ITEM_TYPE
            if (isOrganization) {
                val company = cursor.getStringValue(CommonDataKinds.Organization.COMPANY) ?: ""
                val jobTitle = cursor.getStringValue(CommonDataKinds.Organization.TITLE) ?: ""
                if (company.isNotEmpty() || jobTitle.isNotEmpty()) {
                    val fullName = "$company $jobTitle".trim()
                    val contact = PhoneContact(rawId, contactId, fullName, photoUri, ArrayList(), ArrayList(), ArrayList())
                    contacts.add(contact)
                }
            }
        }
        return contacts
    }

    private fun getContactPhoneNumbers(favoritesOnly: Boolean): ArrayList<PhoneContact> {
        val contacts = ArrayList<PhoneContact>()
        val uri = CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
                Data.RAW_CONTACT_ID,
                Data.CONTACT_ID,
                CommonDataKinds.Phone.NORMALIZED_NUMBER,
                CommonDataKinds.Phone.NUMBER
        )

        val selection = if (favoritesOnly) "${Data.STARRED} = 1" else null

        context.queryCursor(uri, projection, selection) { cursor ->
            val phoneNumber = cursor.getStringValue(CommonDataKinds.Phone.NORMALIZED_NUMBER)
                    ?: cursor.getStringValue(CommonDataKinds.Phone.NUMBER)?.normalizePhoneNumber() ?: return@queryCursor

            val rawId = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val contactId = cursor.getIntValue(Data.CONTACT_ID)
            if (contacts.firstOrNull { it.rawId == rawId } == null) {
                val contact = PhoneContact(rawId, contactId, "", "", ArrayList(), ArrayList(), ArrayList())
                contacts.add(contact)
            }
            contacts.firstOrNull { it.rawId == rawId }?.phoneNumbers?.add(phoneNumber)
        }
        return contacts
    }

    private fun Context.queryCursor(
            uri: Uri,
            projection: Array<String>,
            selection: String? = null,
            selectionArgs: Array<String>? = null,
            sortOrder: String? = null,
            showErrors: Boolean = false,
            callback: (cursor: Cursor) -> Unit
    ) {
        try {
            val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
            cursor?.use {
                if (cursor.moveToFirst()) {
                    do {
                        callback(cursor)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            if (showErrors) {
            }
        }
    }

    private fun String.normalizePhoneNumber() = PhoneNumberUtils.normalizeNumber(this)


    private fun getContactEvents(getBirthdays: Boolean): SparseArray<ArrayList<String>> {
        val eventDates = SparseArray<ArrayList<String>>()
        val uri = Data.CONTENT_URI
        val projection = arrayOf(
                Data.RAW_CONTACT_ID,
                CommonDataKinds.Event.START_DATE
        )

        val selection = "${CommonDataKinds.Event.MIMETYPE} = ? AND ${CommonDataKinds.Event.TYPE} = ?"
        val requiredType = if (getBirthdays) CommonDataKinds.Event.TYPE_BIRTHDAY.toString() else CommonDataKinds.Event.TYPE_ANNIVERSARY.toString()
        val selectionArgs = arrayOf(CommonDataKinds.Event.CONTENT_ITEM_TYPE, requiredType)

        context.queryCursor(uri, projection, selection, selectionArgs) { cursor ->
            val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val startDate = cursor.getStringValue(CommonDataKinds.Event.START_DATE) ?: return@queryCursor

            if (eventDates[id] == null) {
                eventDates.put(id, ArrayList())
            }

            eventDates[id]!!.add(startDate)
        }

        return eventDates
    }

}
