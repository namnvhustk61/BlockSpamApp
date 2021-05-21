package com.stork.blockspam.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.stork.blockspam.AppConfig
import com.stork.blockspam.R
import com.stork.blockspam.database.model.CallPhone.CallPhone
import com.stork.blockspam.database.model.CallPhone.CallPhoneKEY
import com.stork.blockspam.extension.alert
import com.stork.blockspam.extension.showToast
import com.stork.viewcustom.loading.LoadingView


abstract class BaseFragment : Fragment(), BaseView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun showLoading() {
        try {
            (activity as BaseActivity).showLoading()
        } catch (e: Exception) {
        }
    }

    override fun dismissLoading() {
        try {
            (activity as BaseActivity).dismissLoading()
        } catch (e: Exception) {
        }
    }


    //////////////////// Action ///////////////

    fun blockPhone(phone: String, name: String){
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