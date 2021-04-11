package com.stork.blockspam.ui.fraguser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.blockspam.model.PhoneContact
import com.stork.blockspam.utils.AppPermission
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.layout_ask_permission.*

class UserFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
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
        tvGiveNow.setOnClickListener {  PhoneContact.requirePermissions(this) }

        rcvContacts.layoutManager =  LinearLayoutManager(context)
        rcvContacts.adapter =  ContactAdapter<PhoneContact>()
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
}