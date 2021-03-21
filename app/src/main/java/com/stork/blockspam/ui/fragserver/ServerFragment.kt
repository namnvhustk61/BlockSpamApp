package com.stork.blockspam.ui.fragserver

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.blockspam.database.model.DbBlockPhone.DbBlockPhone
import com.stork.http.API
import com.stork.http.API.ApiItf
import com.stork.http.ServiceResult
import com.stork.http.model.BlockPhone
import com.stork.http.thread.RxThread
import kotlinx.android.synthetic.main.fragment_server.*
import java.util.ArrayList

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
        getData(false)
    }

    private fun init(){
        rcvBlockPhone.adapter = ServerAdapter()
        rcvBlockPhone.layoutManager = LinearLayoutManager(context)
    }

    @SuppressLint("ResourceAsColor")
    private fun onEvent(){
        imgReNew.setOnClickListener {
            getData(true)
        }
    }

    private fun getData(isLoading: Boolean) {
        if(isLoading){showLoading()}

        API.getAllBLockPhone(object : ApiItf<List<BlockPhone>>{
            override fun onSuccess(response: ServiceResult<List<BlockPhone>>?) {
                if(isLoading){dismissLoading()}
                if(response?.code == "OK"){
                    (rcvBlockPhone.adapter as ServerAdapter).refresh(response.data)
                    // SAVE DATA TO DB
                   RxThread.onDoInIO {
                       val lsDB:ArrayList<DbBlockPhone> = arrayListOf()
                       response.data.forEachIndexed { index, blockPhone: BlockPhone ->
                           lsDB.add(DbBlockPhone(
                                   index,
                                   blockPhone.phone,
                                   blockPhone.name,
                                   blockPhone.type,
                                   blockPhone.status
                           ))
                       }
                       DbBlockPhone.insertAllDB(context, lsDB)
                   }
                }
            }

            override fun onError(message: String?) {
                if(isLoading){dismissLoading()}
                val dbSaved: List<DbBlockPhone> =  DbBlockPhone.getAllDB(context)
                val lsDB:ArrayList<BlockPhone> = arrayListOf()
                dbSaved.forEach { dbBlockPhone: DbBlockPhone ->
                    lsDB.add(BlockPhone(
                            dbBlockPhone.phone,
                            dbBlockPhone.name,
                            dbBlockPhone.type,
                            dbBlockPhone.status
                    ))
                }
                (rcvBlockPhone.adapter as ServerAdapter).refresh(lsDB)
            }
        })
    }
}