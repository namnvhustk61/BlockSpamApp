package com.stork.blockspam.ui.addcallphone

import android.os.Bundle
import android.widget.Toast
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseActivity
import com.stork.blockspam.database.CallPhone
import com.stork.blockspam.database.CallPhoneKEY
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
             if(_checkEdtInput()){return@setOnClickListener}
            _saveAndExit()
        }
    }

    private fun _checkEdtInput():Boolean{
        if(edtPhone.text.toString() == ""){
            Toast.makeText(this, "Add Phone required not null!", Toast.LENGTH_SHORT).show()
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
        callPhone.insertDB(this)
        finish()
    }
}