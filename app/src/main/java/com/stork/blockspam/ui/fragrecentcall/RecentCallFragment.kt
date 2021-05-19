package com.stork.blockspam.ui.fragrecentcall

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_recent_call.*

class RecentCallFragment:  BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        RecentHelper(context!!).getRecentCalls(true) { recents ->
            activity?.runOnUiThread {
                (rcvRecentCall.adapter as RecentCallAdapter).refresh(recents)
            }
        }
    }

    private fun initView(){
        rcvRecentCall.layoutManager = LinearLayoutManager(context)
        rcvRecentCall.adapter = RecentCallAdapter()
    }
}