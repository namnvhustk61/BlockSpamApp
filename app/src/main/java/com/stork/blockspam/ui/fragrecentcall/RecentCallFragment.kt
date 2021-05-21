package com.stork.blockspam.ui.fragrecentcall

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.blockspam.utils.IntentAction
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
        onEvent()
        refresh()
    }

    override fun onResume() {
        super.onResume()
       refresh()
    }

    private fun refresh(){
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

    private fun onEvent(){
        (rcvRecentCall.adapter as RecentCallAdapter).setOnItemClickListener { item->
            IntentAction.callPhone(activity as AppCompatActivity, item.phoneNumber)
        }

        (rcvRecentCall.adapter as RecentCallAdapter).setOnItemSwipeClickListener_Block { item, index ->
            blockPhone(item.phoneNumber, item.name)
        }

        (rcvRecentCall.adapter as RecentCallAdapter).setOnItemSwipeClickListener_Call{ item, index ->
            IntentAction.callPhone(activity as AppCompatActivity, item.phoneNumber)
        }

        (rcvRecentCall.adapter as RecentCallAdapter).setOnItemSwipeClickListener_Message { item, index ->
            IntentAction.sendSMS(context!!, item.phoneNumber)
        }
    }
}