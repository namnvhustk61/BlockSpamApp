package com.stork.blockspam.ui.addcallphone

import android.os.Bundle
import android.widget.Toast
import com.stork.blockspam.AppConfig
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseActivity
import com.stork.blockspam.database.CallPhone
import com.stork.blockspam.database.CallPhoneKEY
import com.stork.blockspam.extension.alert
import kotlinx.android.synthetic.main.activity_add_call_phone.*

class AddCallPhoneActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_call_phone)

        onEvent()
    }

    private fun onEvent(){
        imgBack.setOnClickListener { finish() }
        tvBtnSave.setOnClickListener {
            popKeyboard()
            if(_checkEdtInput()){return@setOnClickListener}
            _saveAndExit()
        }
    }

    private fun _checkEdtInput():Boolean{
        if(edtPhone.text.toString() == ""){
            this.alert(getString(R.string.all_phone__alert_phone_requied))
            return true
        }
        return false
    }
    private fun _saveAndExit(){
        val callPhone = CallPhone()
        callPhone.phone = edtPhone.text.toString()
        callPhone.name  = edtName.text.toString()
        callPhone.type = CallPhoneKEY.TYPE.TYPE_NORMAL
        callPhone.status = CallPhoneKEY.STATUS.STATUS_BLOCK
        when(callPhone.insertDB(this)){
            AppConfig.SUCCESS   ->{finish()}
            AppConfig.ERROR     ->{this.alert(getString(R.string.all_phone__alert_err_phone_saved))}
            AppConfig.EXCEPTION ->{this.alert(getString(R.string.all_phone__alert_add_excaeption))}
        }
    }
}