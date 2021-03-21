package com.stork.blockspam.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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
}