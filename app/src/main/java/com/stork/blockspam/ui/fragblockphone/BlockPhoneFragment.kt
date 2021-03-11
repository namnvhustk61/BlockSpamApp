package com.stork.blockspam.ui.fragblockphone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.blockspam.database.AppControlDB
import com.stork.blockspam.database.CallPhone
import com.stork.blockspam.navigation.AppNavigation
import kotlinx.android.synthetic.main.fragment_block_phone.*


class BlockPhoneFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_block_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onEvent()
    }

    override fun onResume() {
        super.onResume()

        refreshRCV()
    }
    private fun init(){
        rcvBlockPhone.adapter = BlockPhoneAdapter()
        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemClickListener {
            item -> item.updateDB__changeStatus(context)
        }
        rcvBlockPhone.layoutManager = LinearLayoutManager(context)
    }
    private fun onEvent(){
        imgAdd.setOnClickListener { AppNavigation.toAddCallPhone(context!!)}
        imgMenu.setOnClickListener {}
    }

    private fun refreshRCV(){
        (rcvBlockPhone.adapter as BlockPhoneAdapter).refresh(CallPhone.getAllDB(context))
    }
}