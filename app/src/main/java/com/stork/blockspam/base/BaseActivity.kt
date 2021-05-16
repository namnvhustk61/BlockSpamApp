package com.stork.blockspam.base

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.stork.blockspam.storage.AppSharedPreferences
import com.stork.blockspam.utils.AppPermission
import com.stork.blockspam.utils.AppSettingsManager
import com.stork.viewcustom.loading.LoadingView

abstract class BaseActivity : AppCompatActivity(), BaseView {

    private var loadingView: LoadingView? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        loadingView = LoadingView(this)
//        overridePendingTransition(R.anim.enter_zoom_in, R.anim.enter_zoom_out);
    }

    var focusedView: View? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        popKeyboard()
        return true
    }

    override fun showLoading() {
        try {
            if(loadingView == null){loadingView = LoadingView(this)}
            loadingView?.show()
        } catch (e: Exception) {
        }
    }

    override fun dismissLoading() {
        try {
            loadingView?.cancel()
        } catch (e: Exception) {
        }
    }
    open fun popKeyboard() {
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            focusedView = this.currentFocus
            if (focusedView != null) imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: java.lang.Exception) {
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsManager.ROLE_CALL_SCREENING_ID) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                // Your app is now the call screening app
                /*
                * Permission for BlockSpamService running
                */
                AppSharedPreferences.getInstance(this).saveBoolean(AppSharedPreferences.KEY_PREFERRENCE.IS_DEFAULT_BLOCK_APP, true)
                checkPermissionForBlockSpamService()
            } else {
                // Your app is not the call screening app
                /*
                * Permission for BlockCallBroadcastReceiver running
                */
                AppSharedPreferences.getInstance(this).saveBoolean(AppSharedPreferences.KEY_PREFERRENCE.IS_DEFAULT_BLOCK_APP, false)
                checkPermissionForBlockBroadcast()
            }
        }

        if (requestCode == AppSettingsManager.ROLE_CALL_DIAL_ID) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                // Your app is now the call screening app
                onStatusPermissionListener?.invoke(true)
            } else {
                // Your app is not the call screening app
                onStatusPermissionListener?.invoke(false)
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AppPermission.onRequestPermissionsResult(requestCode, permissions, grantResults) {state: Boolean ->
            AppSharedPreferences.getInstance(this).saveString(
                    AppSharedPreferences.KEY_PREFERRENCE.IS_PER_BLOCK,
                    state.toString()
            )
        }
    }

    ////////////// Permission /////////////
    fun setPermissionV1(){
        /*
        *  Required  App is  caller id & spam app default  OR  Call Phone Default
        *   at some android SDK slowly  don't  have caller id & spam app default
        *   then require  Call Phone Default
        * */

        /* set APP is caller id & spam app default
         *
         *  status == true  -> device have caller id & spam app default
         *  status == false -> device don't have caller id & spam app default
         */
        val status :Boolean = AppSettingsManager.setDefaultAppBlockCall(this)
        AppSharedPreferences.getInstance(this).saveString(
                AppSharedPreferences.KEY_PREFERRENCE.IS_DEFAULT_BLOCK_APP,
                status.toString()
        )
        if(!status){
            /*
            * Permission for BlockCallBroadcastReceiver running
            */
            checkPermissionForBlockBroadcast()
        }
    }

    private var onStatusPermissionListener: ((status: Boolean)->Unit)? = null
    fun setPermissionV2(onStatusListener: ((status: Boolean)->Unit)?){
        this.onStatusPermissionListener = onStatusListener;
        // ask permission set default DIAL
        AppSettingsManager.setDefaultAppCallPhone(this)
    }

    fun checkPermissionForBlockSpamService(){
        AppPermission.requirePermissions(
                this,
                arrayOf(AppPermission.PER_READ_CONTACTS, AppPermission.PER_READ_PHONE_STATE),
                AppPermission.PER_REQUEST_CODE
        )
    }

    fun checkPermissionForBlockBroadcast(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppPermission.requirePermissions(
                    this,
                    arrayOf(
                            AppPermission.PER_READ_PHONE_STATE,
                            AppPermission.PER_READ_CALL_LOG,
                            AppPermission.PER_ANSWER_PHONE_CALLS // >=26
                    ),
                    AppPermission.PER_REQUEST_CODE
            )
        }else{
            AppPermission.requirePermissions(
                    this,
                    arrayOf(
                            AppPermission.PER_READ_PHONE_STATE,
                            AppPermission.PER_READ_CALL_LOG
                    ),
                    AppPermission.PER_REQUEST_CODE
            )
        }
    }
}