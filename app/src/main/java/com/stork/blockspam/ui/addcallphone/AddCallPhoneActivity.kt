package com.stork.blockspam.ui.addcallphone

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.stork.blockspam.AppConfig
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseActivity
import com.stork.blockspam.database.model.CallPhone.CallPhone
import com.stork.blockspam.database.model.CallPhone.CallPhoneKEY
import com.stork.blockspam.extension.alert
import com.stork.blockspam.utils.AppSettingsManager
import com.stork.blockspam.utils.BlockContact
import com.stork.viewcustom.popup.PopupWindowMenu
import kotlinx.android.synthetic.main.activity_add_call_phone.*
import kotlinx.android.synthetic.main.activity_add_call_phone.imgMenu

class AddCallPhoneActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_call_phone)

        onEvent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsManager.ROLE_CALL_DIAL_ID) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                // Your app is now the call screening app
                addBlockList2DB()
            } else {
                // Your app is not the call screening app
            }
        }
    }

    private fun onEvent(){
        imgBack.setOnClickListener { finish() }

        tvBtnSave.setOnClickListener {
            popKeyboard()
            if(_checkEdtInput()){return@setOnClickListener}
            val status =  _saveAndExit(
                    edtPhone.text.toString(),
                    edtName.text.toString()
            )
            when(status){
                AppConfig.SUCCESS   ->{finish()}
                AppConfig.ERROR     ->{this.alert(getString(R.string.all_phone__alert_err_phone_saved))}
                AppConfig.EXCEPTION ->{this.alert(getString(R.string.all_phone__alert_add_excaeption))}
            }
        }

        imgMenu.setOnClickListener {
            showOptionsMenu(it, PopupWindowMenu.Itf { index ->
                when(index){
                    0->{getBlockList()}
                }
            })
        }
    }

    private fun _checkEdtInput():Boolean{
        if(edtPhone.text.toString() == ""){
            this.alert(getString(R.string.all_phone__alert_phone_requied))
            return true
        }
        return false
    }
    private fun _saveAndExit(phone: String, name: String): Int{
        val callPhone = CallPhone()
        callPhone.phone = phone
        callPhone.name  = name
        callPhone.type = CallPhoneKEY.TYPE.TYPE_NORMAL
        callPhone.status = CallPhoneKEY.STATUS.STATUS_BLOCK
       return callPhone.insertDB(this)
    }

    private var lsTitle: List<String>? = null
    private var lsIcons: List<Drawable?>? =null
    private var popupMenu :PopupWindowMenu?= null
    private fun showOptionsMenu(view: View, onclickItem: PopupWindowMenu.Itf){
        if(popupMenu == null){
            lsTitle = listOf(getString(R.string.add_from_block_list))
            lsIcons = listOf(ContextCompat.getDrawable(this, R.drawable.ic_block_list_local))
            popupMenu = PopupWindowMenu(
                    this, lsTitle, lsIcons,
                    ContextCompat.getColor(this, R.color.background_item),
                    onclickItem
            )
        }

        popupMenu?.showAsDropDown(view, 0, -70)
    }

    private fun getBlockList(){
        AppSettingsManager.setDefaultAppCallPhone(this)
    }

    private fun addBlockList2DB(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)  {
            val lsBlockPhone: List<String> = BlockContact.get(this)
            var count: Int = 0
            lsBlockPhone.forEach { phone: String ->
                if(_saveAndExit(phone, getString(R.string.block_list) + count) == AppConfig.SUCCESS){
                    count++
                }
            }
            this.alert(getString(R.string.you_added_number_block_phones, count.toString()))
        }
    }
}