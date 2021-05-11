package com.stork.blockspam.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.net.Uri
import android.telecom.PhoneAccountHandle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.stork.blockspam.R
import com.stork.blockspam.extension.telecomManager
import com.stork.blockspam.model.SIMAccount
import kotlinx.android.synthetic.main.dialog_select_sim.view.*

@SuppressLint("MissingPermission")
class SelectSIMDialog(val activity: AppCompatActivity, val phoneNumber: String, val callback: (handle: PhoneAccountHandle?) -> Unit) {
    private var dialog: AlertDialog? = null
    private val view = activity.layoutInflater.inflate(R.layout.dialog_select_sim, null)

    init {
        val radioGroup = view.select_sim_radio_group

        activity.getAvailableSIMCardLabels().forEachIndexed { index, SIMAccount ->
            val radioButton = (activity.layoutInflater.inflate(R.layout.part_radio_button, null) as RadioButton).apply {
                text = SIMAccount.label
                id = index
                setOnClickListener { selectedSIM(SIMAccount.handle, SIMAccount.label) }
            }
            radioGroup!!.addView(radioButton, RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }

        dialog = AlertDialog.Builder(activity)
            .setOnCancelListener { callback.invoke(null) }
            .create().apply {
                activity.setupDialogStuff(view, this)
            }
    }

    private fun selectedSIM(handle: PhoneAccountHandle, label: String) {
        callback(handle)
        dialog?.dismiss()
    }
}

@SuppressLint("MissingPermission")
fun Context.getAvailableSIMCardLabels(): ArrayList<SIMAccount> {
    val SIMAccounts = ArrayList<SIMAccount>()
    try {
        telecomManager.callCapablePhoneAccounts.forEachIndexed { index, account ->
            val phoneAccount = telecomManager.getPhoneAccount(account)
            var label = phoneAccount.label.toString()
            var address = phoneAccount.address.toString()
            if (address.startsWith("tel:") && address.substringAfter("tel:").isNotEmpty()) {
                address = Uri.decode(address.substringAfter("tel:"))
                label += " ($address)"
            }

            val SIM = SIMAccount(index + 1, phoneAccount.accountHandle, label, address.substringAfter("tel:"))
            SIMAccounts.add(SIM)
        }
    } catch (ignored: Exception) {
    }
    return SIMAccounts
}

@SuppressLint("UseCompatLoadingForDrawables")
fun Activity.setupDialogStuff(view: View, dialog: AlertDialog, titleId: Int = 0, titleText: String = "", callback: (() -> Unit)? = null) {
    if (isDestroyed || isFinishing) {
        return
    }

    dialog.apply {
        setView(view)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(true)
        show()
        getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorPrimary))
        getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorPrimary))
        getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getColor(R.color.colorPrimary))
        val bgDrawable = getDrawable(R.drawable.bg_state_pressed)
        bgDrawable?.mutate()?.setColorFilter(getColor(R.color.colorBgGray), PorterDuff.Mode.SRC_ATOP)
        window?.setBackgroundDrawable(bgDrawable)
    }
    callback?.invoke()
}