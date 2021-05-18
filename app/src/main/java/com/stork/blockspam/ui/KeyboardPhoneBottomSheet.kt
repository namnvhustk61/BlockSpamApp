  package com.stork.blockspam.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stork.blockspam.R
import com.stork.blockspam.extension.*
import com.stork.blockspam.utils.IntentAction
import kotlinx.android.synthetic.main.bottom_sheet_keyboard_phone.*
import kotlinx.android.synthetic.main.layout_dialpad.*

class KeyboardPhoneBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "KeyboardPhoneBottomSheet"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetBackgroundWhite)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_keyboard_phone, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView(){
        dialpad_0_holder.setOnClickListener { dialpadPressed('0', it) }
        dialpad_1.setOnClickListener { dialpadPressed('1', it) }
        dialpad_2.setOnClickListener { dialpadPressed('2', it) }
        dialpad_3.setOnClickListener { dialpadPressed('3', it) }
        dialpad_4.setOnClickListener { dialpadPressed('4', it) }
        dialpad_5.setOnClickListener { dialpadPressed('5', it) }
        dialpad_6.setOnClickListener { dialpadPressed('6', it) }
        dialpad_7.setOnClickListener { dialpadPressed('7', it) }
        dialpad_8.setOnClickListener { dialpadPressed('8', it) }
        dialpad_9.setOnClickListener { dialpadPressed('9', it) }

        dialpad_1.setOnLongClickListener { speedDial(1); true }
        dialpad_2.setOnLongClickListener { speedDial(2); true }
        dialpad_3.setOnLongClickListener { speedDial(3); true }
        dialpad_4.setOnLongClickListener { speedDial(4); true }
        dialpad_5.setOnLongClickListener { speedDial(5); true }
        dialpad_6.setOnLongClickListener { speedDial(6); true }
        dialpad_7.setOnLongClickListener { speedDial(7); true }
        dialpad_8.setOnLongClickListener { speedDial(8); true }
        dialpad_9.setOnLongClickListener { speedDial(9); true }

        dialpad_0_holder.setOnLongClickListener { dialpadPressed('+', null); true }
        dialpad_asterisk.setOnClickListener { dialpadPressed('*', it) }
        dialpad_hashtag.setOnClickListener { dialpadPressed('#', it) }
        dialpad_clear_char.setOnClickListener { clearChar(it) }
        dialpad_clear_char.setOnLongClickListener { clearInput(); true }
        dialpad_call_button.setOnClickListener {
            if(dialpad_input.text.isNotEmpty()){
                IntentAction.callPhone(activity as AppCompatActivity, dialpad_input.text.toString())
            }
        }
        dialpad_input.onTextChangeListener { dialpadValueChanged(it) }

        disableKeyboardPopping()
    }

    private fun dialpadPressed(char: Char, view: View?) {
        dialpad_input.addCharacter(char)
        view?.performHapticFeedback()
    }

    private fun clearChar(view: View) {
        dialpad_input.dispatchKeyEvent(dialpad_input.getKeyEvent(KeyEvent.KEYCODE_DEL))
        view.performHapticFeedback()
    }

    private fun clearInput() {
        dialpad_input.setText("")
    }

    private fun disableKeyboardPopping() {
        dialpad_input.showSoftInputOnFocus = false
    }

    private fun speedDial(id: Int) {
        if (dialpad_input.value.isEmpty()) {

        }
    }

    fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    private fun dialpadValueChanged(text: String) {
        val len = text.length
        if (len > 8 && text.startsWith("*#*#") && text.endsWith("#*#*")) {
            val secretCode = text.substring(4, text.length - 4)
            if (isOreoPlus()) {
                if (context!!.isDefaultDialer()) {
                    context?.getSystemService(TelephonyManager::class.java)?.sendDialerSpecialCode(secretCode)
                } else {

                }
            } else {
                val intent = Intent(Telephony.Sms.Intents.SECRET_CODE_ACTION, Uri.parse("android_secret_code://$secretCode"))
                context?.sendBroadcast(intent)
            }
            return
        }

        if(len>0){
            dialpad_clear_char.visibility = View.VISIBLE
            dialpad_input.visibility = View.VISIBLE
        }else{
            dialpad_clear_char.visibility = View.INVISIBLE
            dialpad_input.visibility = View.GONE
        }
    }
}