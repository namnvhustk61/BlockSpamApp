package com.stork.blockspam.ui.fragserver

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.http.API
import com.stork.http.API.ApiItf
import com.stork.http.ServiceResult
import com.stork.http.model.BlockPhone
import kotlinx.android.synthetic.main.fragment_block_phone.*
import kotlinx.android.synthetic.main.fragment_block_phone.rcvBlockPhone
import kotlinx.android.synthetic.main.fragment_server.*

class ServerFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_server, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onEvent()
        getData()

    }

    private fun init(){
        rcvBlockPhone.adapter = ServerAdapter()
        rcvBlockPhone.layoutManager = LinearLayoutManager(context)
    }

    @SuppressLint("ResourceAsColor")
    private fun onEvent(){
        imgReNew.setOnClickListener {
            getData()
        }
    }

    private fun getData() {
        API.getAllBLockPhone(object : ApiItf<List<BlockPhone>>{
            override fun onSuccess(response: ServiceResult<List<BlockPhone>>?) {
                if(response?.code == "OK"){
                    (rcvBlockPhone.adapter as ServerAdapter).refresh(response.data)
                }
            }

            override fun onError(message: String?) {

            }
        })
    }
}