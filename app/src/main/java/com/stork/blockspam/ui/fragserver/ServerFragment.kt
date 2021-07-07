package com.stork.blockspam.ui.fragserver

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.stork.blockspam.AppConfig
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.blockspam.database.model.CallPhone.CallPhone
import com.stork.blockspam.database.model.CallPhone.CallPhoneKEY
import com.stork.blockspam.database.model.DbBlockPhone.DbBlockPhone
import com.stork.blockspam.service.foreground.AppForegroundService
import com.stork.blockspam.storage.AppSharedPreferences

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
        imgCloseStateLongClick.setOnClickListener {
            openStateLongClick(false)
        }

        imgSwCheckAll.setOnClickListener {
            setSelectAll(imgSwCheckAll.changeStateActive())
        }

        imgReNew.setOnClickListener {
            getData(true)
        }

        imgAddAll.setOnClickListener {
            val lsDB:ArrayList<BlockPhone> = arrayListOf()
            (rcvBlockPhone.adapter as ServerAdapter)._items.forEach { item: BlockPhone ->
                if(item.status == CallPhoneKEY.STATUS.STATUS_BLOCK){
                    if(!addPhoneToBlockList(item)){
                        lsDB.add(item)
                    }
                }else{
                    lsDB.add(item)
                }
            }
            (rcvBlockPhone.adapter as ServerAdapter).refresh(lsDB)
            openStateLongClick(false)
        }

        (rcvBlockPhone.adapter as ServerAdapter).setOnItemLongClickListener { item ->
            update_StatusItem(item)

            openStateLongClick(true)

        }
        (rcvBlockPhone.adapter as ServerAdapter).setOnItemClickListener { item ->
            update_StatusItem(item)
        }
        (rcvBlockPhone.adapter as ServerAdapter).setOnItemSwipeClickListener {
            item, index ->
            if(addPhoneToBlockList(item)){
                (rcvBlockPhone.adapter as ServerAdapter)._items.removeAt(index)
                rcvBlockPhone.adapter?.notifyItemRemoved(index)
            }
        }

        swStatus.isChecked = AppSharedPreferences.getInstance(context!!).getString(
            AppSharedPreferences.KEY_PREFERRENCE.SERVICE_RUNNING) == "true"

        swStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                // start foreground
                AppForegroundService.startService(activity)
                AppSharedPreferences.getInstance(context!!).saveString(
                    AppSharedPreferences.KEY_PREFERRENCE.SERVICE_RUNNING,
                    "true"
                )
            }else{
                // start foreground
                AppForegroundService.stopForegroundService(activity)
                AppSharedPreferences.getInstance(context!!).saveString(
                    AppSharedPreferences.KEY_PREFERRENCE.SERVICE_RUNNING,
                    "false"
                )
            }
        }
    }

    private fun openStateLongClick(open: Boolean){
        (rcvBlockPhone.adapter as ServerAdapter).setonStateLongCLick(open)
        imgSwCheckAll.setActive(false)

        if(open){
            imgCloseStateLongClick.visibility = View.VISIBLE
            tvTitle.visibility                = View.GONE

            imgReNew.visibility                = View.GONE

            llStatusLongClick.visibility = View.VISIBLE
        }else{
            imgCloseStateLongClick.visibility = View.GONE
            tvTitle.visibility                = View.VISIBLE

            imgReNew.visibility               = View.VISIBLE

            llStatusLongClick.visibility      = View.GONE
            setSelectAll(false)
        }
    }

    private fun update_StatusItem(item: BlockPhone){
        if(item.status == CallPhoneKEY.STATUS.STATUS_BLOCK){
            item.status = CallPhoneKEY.STATUS.STATUS_UNBLOCK
        }else{
            item.status = CallPhoneKEY.STATUS.STATUS_BLOCK
        }
    }

    private fun setSelectAll(bool: Boolean){
        if(bool){
            // select All
            (rcvBlockPhone.adapter as ServerAdapter)._items.forEach{item: BlockPhone? ->
                item?.status = CallPhoneKEY.STATUS.STATUS_BLOCK
            }
        }else{
            // dismiss select All
            (rcvBlockPhone.adapter as ServerAdapter)._items.forEach { item: BlockPhone? ->
                item?.status = CallPhoneKEY.STATUS.STATUS_UNBLOCK
            }
        }
        (rcvBlockPhone.adapter as ServerAdapter).notifyDataSetChanged()
    }

    private fun getData(isLoading: Boolean) {
        if(isLoading){showLoading()}

        API.getAllBLockPhone(object : ApiItf<List<BlockPhone>>{
            override fun onSuccess(response: ServiceResult<List<BlockPhone>>?) {
                if(isLoading){dismissLoading()}
                if(response?.code == "OK"){
                    refreshRCV(ArrayList<BlockPhone>(response.data))
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
                refreshRCV(lsDB)
            }
        })
    }

    private fun refreshRCV(list :ArrayList<BlockPhone>){
        val valueNew :ArrayList<BlockPhone> = arrayListOf()
        for (index in 0 until list.size){
            if(CallPhone.hasDB(context, list[index].phone)){
                continue
            }
            valueNew.add(list[index])

        }

        (rcvBlockPhone.adapter as ServerAdapter).refresh(valueNew)
    }

    private fun addPhoneToBlockList(item: BlockPhone): Boolean{
        val callPhone = CallPhone()
        callPhone.phone = item.phone
        callPhone.name  = item.name
        callPhone.type = "${CallPhoneKEY.TYPE.TYPE_ONLINE}-${item.type}"
        callPhone.status = CallPhoneKEY.STATUS.STATUS_BLOCK

        return when(callPhone.insertDB(context)){
            AppConfig.SUCCESS   ->{
                true
            }
            AppConfig.ERROR, AppConfig.EXCEPTION->{
                false
            }
            else-> false
        }
    }

}