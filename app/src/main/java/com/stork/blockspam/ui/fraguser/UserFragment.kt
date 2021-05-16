package com.stork.blockspam.ui.fraguser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.stork.blockspam.AppConfig
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.blockspam.database.model.CallPhone.CallPhone
import com.stork.blockspam.database.model.CallPhone.CallPhoneKEY
import com.stork.blockspam.extension.alert
import com.stork.blockspam.extension.showToast
import com.stork.blockspam.model.PhoneContact
import com.stork.blockspam.utils.AppPermission
import com.stork.blockspam.utils.IntentAction
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.layout_ask_permission.*
import kotlinx.android.synthetic.main.layout_ask_permission.view.*

class UserFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        onEvent()
        if(PhoneContact.requirePermissions(this)){
            setDataRCV()
        }
    }

    override fun onResume() {
        super.onResume()
        showAskLayout(!PhoneContact.checkPermission(context!!))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AppPermission.onRequestPermissionsResult(requestCode, permissions, grantResults) { state: Boolean ->
          if(state){
              showAskLayout(false)
              setDataRCV()
          }
        }
    }

    private fun initView() {
        tvDismiss.setOnClickListener { showAskLayout(false)}
        tvGiveDialPhoneNow.setOnClickListener {  PhoneContact.requirePermissions(this) }

        rcvContacts.layoutManager =  LinearLayoutManager(context)
        rcvContacts.adapter =  ContactAdapter<PhoneContact>()
    }

    private fun onEvent(){
        (rcvContacts.adapter as ContactAdapter<*>).setOnItemClickListener {item->
            IntentAction.callPhone(context!!, item.phoneNumbers[0])
        }

        (rcvContacts.adapter as ContactAdapter<*>).setOnItemSwipeClickListener_Block {item, index ->
            blockPhone(item.phoneNumbers[0], item.name)
        }

        (rcvContacts.adapter as ContactAdapter<*>).setOnItemSwipeClickListener_Call{item, index ->
            IntentAction.callPhone(context!!, item.phoneNumbers[0])
        }

        (rcvContacts.adapter as ContactAdapter<*>).setOnItemSwipeClickListener_Message { item, index ->
            IntentAction.sendSMS(context!!, item.phoneNumbers[0])
        }
    }

    private fun showAskLayout(bool: Boolean){
        if (bool){
            loAskPer.visibility = View.VISIBLE
        }else{
            loAskPer.visibility = View.GONE
        }
    }

    private fun setDataRCV(){
        (rcvContacts.adapter as ContactAdapter<PhoneContact>).refresh(PhoneContact.getContacts(context!!, false))
    }


    private fun blockPhone(phone: String, name: String){
        val callPhone = CallPhone()
        callPhone.phone = phone
        callPhone.name  = name
        callPhone.type = CallPhoneKEY.TYPE.TYPE_LOCAL
        callPhone.status = CallPhoneKEY.STATUS.STATUS_BLOCK
        val status = callPhone.insertDB(context)
        when(status){
            AppConfig.SUCCESS   ->{this.showToast(getString(R.string.block_successfully))}
            AppConfig.ERROR     ->{this.alert(getString(R.string.all_phone__alert_err_phone_saved))}
            AppConfig.EXCEPTION ->{this.alert(getString(R.string.all_phone__alert_add_excaeption))}
        }
    }
}