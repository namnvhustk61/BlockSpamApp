package com.stork.blockspam.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.stork.blockspam.R
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
}