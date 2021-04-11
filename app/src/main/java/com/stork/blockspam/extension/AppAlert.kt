package com.stork.blockspam.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import com.stork.blockspam.R


const val BTN_TEXT_OK :String = "OK"
    const val BTN_TEXT_CANCEL :String = "CANCEL"
    fun Activity.alert(message: String) {
        alert(null, message, BTN_TEXT_OK)
    }

    fun Activity.alert(message: String, function: (DialogInterface) -> Unit) {
        alert(null, message, BTN_TEXT_OK, false, function)
    }

    fun Activity.alert(title: String?, message: String) {
        alert(title, message, BTN_TEXT_OK)
    }

    fun Activity.alert(title: String?, message: String, buttonText: String) {
        alert(title, message, buttonText, true) { it.cancel() }
    }

    fun Activity.alert(
        title: String? = null,
        message: String = "",
        buttonText: String = BTN_TEXT_OK,
        cancelable: Boolean = true,
        function: (DialogInterface) -> Unit
    ) {
        try {
            val builder = AlertDialog.Builder(this)
            if (title != null) {
                builder.setTitle(title)
            }
            builder.setMessage(message)
            builder.setNegativeButton(buttonText) { dialogInterface, _ ->
                function(dialogInterface)
            }
            builder.setCancelable(cancelable)
            val dialog = builder.create()

            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorPrimary))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.gray))
        }catch (e : Exception){

        }
    }

fun Activity.alert(
        title: String? = null,
        message: String = "",
        button_OK_Text: String = BTN_TEXT_OK,
        button_Cacel_Text: String = BTN_TEXT_CANCEL,
        cancelable: Boolean = true,
        function: (DialogInterface) -> Unit
) {
    try {
        val builder = AlertDialog.Builder(this)
        if (title != null) {
            builder.setTitle(title)
        }
        builder.setMessage(message)
        builder.setNegativeButton(button_OK_Text) { dialogInterface, _ ->
            function(dialogInterface)
        }
        builder.setPositiveButton(button_Cacel_Text) { dialogInterface, _ ->
        }
        builder.setCancelable(cancelable)

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorPrimary))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.gray))
    }catch (e : Exception){

    }
}

    fun Activity.alert(title: String, message: String, function: (DialogInterface) -> Unit) {
        alert(title, message, BTN_TEXT_OK, true, function)
    }

    fun Activity.alert(message: String, cancelable: Boolean, function: (DialogInterface) -> Unit) {
        alert(null, message, BTN_TEXT_OK, cancelable, function)
    }

    fun Activity.alert(title: String, message: String, cancel: Boolean, function: (DialogInterface) -> Unit) {
        if(cancel){
            alert(title, message, BTN_TEXT_OK, BTN_TEXT_CANCEL, true, function)
        }else{
            alert(title, message, BTN_TEXT_OK, true, function)
        }
    }

    fun Activity.alert(message: String, cancelable: Boolean, cancel: Boolean,  function: (DialogInterface) -> Unit) {
        if(cancel){
            alert(null, message, BTN_TEXT_OK, BTN_TEXT_CANCEL, cancelable, function)
        }else{
            alert(null, message, BTN_TEXT_OK, cancelable, function)
        }

    }

    fun Fragment.alert(message: String) {
        activity?.alert(message)
    }

    fun Fragment.alert(message: String, cancelable: Boolean, function: (DialogInterface) -> Unit) {
        activity?.alert(null, message, BTN_TEXT_OK, cancelable, function)
    }

    fun Fragment.alert(title: String, message: String, cancel: Boolean, function: (DialogInterface) -> Unit) {
        if(cancel){
            activity?.alert(title, message, BTN_TEXT_OK, BTN_TEXT_CANCEL, true, function)
        }else{
            activity?.alert(title, message, BTN_TEXT_OK, true, function)
        }
    }

    fun Fragment.alert(message: String, cancelable: Boolean, cancel: Boolean,  function: (DialogInterface) -> Unit) {
        if(cancel){
            activity?.alert(null, message, BTN_TEXT_OK, BTN_TEXT_CANCEL, cancelable, function)
        }else{
            activity?.alert(null, message, BTN_TEXT_OK, cancelable, function)
        }

    }

