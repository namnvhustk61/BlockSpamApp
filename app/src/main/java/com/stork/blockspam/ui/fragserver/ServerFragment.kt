package com.stork.blockspam.ui.fragserver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.http.API
import com.stork.http.API.ApiItf
import com.stork.http.ServiceResult
import com.stork.http.model.BlockPhone

class ServerFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_server, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        API.getAllBLockPhone(object : ApiItf<List<BlockPhone>>{
            override fun onSuccess(response: ServiceResult<List<BlockPhone>>?) {
                val x = ""
            }

            override fun onError(message: String?) {

            }
        })
    }
}