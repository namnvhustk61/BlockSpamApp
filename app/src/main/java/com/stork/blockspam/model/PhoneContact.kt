package com.stork.blockspam.model

import android.app.Activity
import android.content.Context
import android.provider.ContactsContract
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.stork.blockspam.extension.getIntValue
import com.stork.blockspam.extension.getStringValue
import com.stork.blockspam.extension.queryCursor
import com.stork.blockspam.utils.AppPermission

data class PhoneContact(
        val rawId: Int,
        val contactId: Int,
        var name: String,
        var photoUri: String,
        var phoneNumbers: ArrayList<String>,
        var birthdays: ArrayList<String>,
        var anniversaries: ArrayList<String>
) : Comparable<PhoneContact> {

    override fun compareTo(other: PhoneContact): Int {
        val firstString = name
        val secondString = other.name

        return if (firstString.firstOrNull()?.isLetter() == true && secondString.firstOrNull()?.isLetter() == false) {
            -1
        } else if (firstString.firstOrNull()?.isLetter() == false && secondString.firstOrNull()?.isLetter() == true) {
            1
        } else {
            if (firstString.isEmpty() && secondString.isNotEmpty()) {
                1
            } else if (firstString.isNotEmpty() && secondString.isEmpty()) {
                -1
            } else {
                firstString.compareTo(secondString, true)
            }
        }
    }

     companion object{
         val requestCode: Int = 99
         fun checkPermission(context: Context): Boolean{
             return AppPermission.hasPermission(context, AppPermission.PER_READ_CONTACTS)
         }

         fun requirePermissions(activity: Activity): Boolean{
             return AppPermission.requirePermissions(activity, AppPermission.PER_READ_CONTACTS, requestCode)
         }

         fun requirePermissions(fragment: Fragment): Boolean{

             return AppPermission.requirePermissions(fragment, AppPermission.PER_READ_CONTACTS, requestCode)
         }

         fun getContacts(context: Context, favoritesOnly: Boolean): List<PhoneContact>{
             if(!checkPermission(context)){
                 return arrayListOf()
             }

             val names = getContactNames(context, favoritesOnly)
             var allContacts = getContactPhoneNumbers(context, favoritesOnly)

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
             allContacts.sort()

             return allContacts
         }

         private fun getContactNames(context: Context, favoritesOnly: Boolean): List<PhoneContact> {
             val contacts = ArrayList<PhoneContact>()
             val startNameWithSurname = false
             val uri = ContactsContract.Data.CONTENT_URI
             val projection = arrayOf(
                     ContactsContract.Data.RAW_CONTACT_ID,
                     ContactsContract.Data.CONTACT_ID,
                     ContactsContract.CommonDataKinds.StructuredName.PREFIX,
                     ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                     ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                     ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                     ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
                     ContactsContract.CommonDataKinds.StructuredName.PHOTO_THUMBNAIL_URI,
                     ContactsContract.CommonDataKinds.Organization.COMPANY,
                     ContactsContract.CommonDataKinds.Organization.TITLE,
                     ContactsContract.Data.MIMETYPE
             )

             var selection = "(${ContactsContract.Data.MIMETYPE} = ? OR ${ContactsContract.Data.MIMETYPE} = ?)"

             if (favoritesOnly) {
                 selection += " AND ${ContactsContract.Data.STARRED} = 1"
             }

             val selectionArgs = arrayOf(
                     ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                     ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
             )

             context.queryCursor(uri, projection, selection, selectionArgs) { cursor ->
                 val rawId = cursor.getIntValue(ContactsContract.Data.RAW_CONTACT_ID)
                 val contactId = cursor.getIntValue(ContactsContract.Data.CONTACT_ID)
                 val mimetype = cursor.getStringValue(ContactsContract.Data.MIMETYPE)
                 val photoUri = cursor.getStringValue(ContactsContract.CommonDataKinds.StructuredName.PHOTO_THUMBNAIL_URI) ?: ""
                 val isPerson = mimetype == ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                 if (isPerson) {
                     val prefix = cursor.getStringValue(ContactsContract.CommonDataKinds.StructuredName.PREFIX) ?: ""
                     val firstName = cursor.getStringValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME) ?: ""
                     val middleName = cursor.getStringValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME) ?: ""
                     val familyName = cursor.getStringValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME) ?: ""
                     val suffix = cursor.getStringValue(ContactsContract.CommonDataKinds.StructuredName.SUFFIX) ?: ""
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

                 val isOrganization = mimetype == ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                 if (isOrganization) {
                     val company = cursor.getStringValue(ContactsContract.CommonDataKinds.Organization.COMPANY) ?: ""
                     val jobTitle = cursor.getStringValue(ContactsContract.CommonDataKinds.Organization.TITLE) ?: ""
                     if (company.isNotEmpty() || jobTitle.isNotEmpty()) {
                         val fullName = "$company $jobTitle".trim()
                         val contact = PhoneContact(rawId, contactId, fullName, photoUri, ArrayList(), ArrayList(), ArrayList())
                         contacts.add(contact)
                     }
                 }
             }
             return contacts
         }

         private fun getContactPhoneNumbers(context: Context, favoritesOnly: Boolean): ArrayList<PhoneContact> {

             val contacts = ArrayList<PhoneContact>()
             val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
             val projection = arrayOf(
                     ContactsContract.Data.RAW_CONTACT_ID,
                     ContactsContract.Data.CONTACT_ID,
                     ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                     ContactsContract.CommonDataKinds.Phone.NUMBER
             )

             val selection = if (favoritesOnly) "${ContactsContract.Data.STARRED} = 1" else null

             context.queryCursor(uri, projection, selection) { cursor ->
                 val phoneNumber = cursor.getStringValue(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                         ?: cursor.getStringValue(ContactsContract.CommonDataKinds.Phone.NUMBER) ?: return@queryCursor

                 val rawId = cursor.getIntValue(ContactsContract.Data.RAW_CONTACT_ID)
                 val contactId = cursor.getIntValue(ContactsContract.Data.CONTACT_ID)
                 if (contacts.firstOrNull { it.rawId == rawId } == null) {
                     val contact = PhoneContact(rawId, contactId, "", "", ArrayList(), ArrayList(), ArrayList())
                     contacts.add(contact)
                 }
                 contacts.firstOrNull { it.rawId == rawId }?.phoneNumbers?.add(phoneNumber)
             }
             return contacts
         }
     }

}