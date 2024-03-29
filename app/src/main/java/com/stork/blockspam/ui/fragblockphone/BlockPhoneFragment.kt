package com.stork.blockspam.ui.fragblockphone

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Secure.ANDROID_ID
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.blockspam.database.model.CallPhone.CallPhone
import com.stork.blockspam.database.model.CallPhone.CallPhoneKEY
import com.stork.blockspam.extension.alert
import com.stork.blockspam.navigation.AppNavigation
import com.stork.blockspam.storage.AppSharedPreferences
import com.stork.blockspam.ui.MainActivity
import com.stork.blockspam.utils.AppSettingsManager
import com.stork.http.API
import com.stork.http.ServiceResult
import com.stork.http.model.AddPhoneCloud
import com.stork.http.model.BlockPhoneRes
import com.stork.viewcustom.popup.PopupWindowMenu
import kotlinx.android.synthetic.main.fragment_block_phone.*
import java.util.*


class BlockPhoneFragment : BaseFragment() {

    private var lsTitle: List<String>? = null
    private var lsIcons: List<Drawable?>? =null
    private var popupMenu :PopupWindowMenu?= null

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
        /*
        * if app is not default Dial App
        * then show item in recycle to user choose settings default app
        * 1. Default DIAL APP
        * 2. Default BLOCK APP
        * */
         (rcvBlockPhone.adapter as BlockPhoneAdapter)
                .set_isShow_Ask_Permission(!AppSettingsManager.isDefaultDialer(context!!))

        refreshRCV()
    }
    private fun init(){
        rcvBlockPhone.adapter = BlockPhoneAdapter()
        rcvBlockPhone.layoutManager = LinearLayoutManager(context)
    }
    @SuppressLint("ResourceAsColor")
    private fun onEvent(){
        imgAdd.setOnClickListener { AppNavigation.toAddCallPhone(context!!)}
        imgMenu.setOnClickListener {
            showOptionsMenu(it, PopupWindowMenu.Itf { index ->
                when(index){
                    0->{menuClickSelectAll()}
                    1->{menuClickIgnoreAll()}
                    2->{menuClickDeleteAll()}
                }
            })
        }

        tvCancel.setOnClickListener {
            _setonStateDeleteItem(false)
            (rcvBlockPhone.adapter as BlockPhoneAdapter).setonStateDeleteItem(false)
        }

        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemLongClickListener { _ ->
            (rcvBlockPhone.adapter as BlockPhoneAdapter).setonStateDeleteItem(true)
           _setonStateDeleteItem(true)
        }

        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemClickListener {
                item -> item.updateDB__changeStatus(context)
        }

        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemDeleteClickListener { item, pos ->
            if(item.deleteDb(context)){
                (rcvBlockPhone.adapter as BlockPhoneAdapter).deleteItem(pos, item) { newLsItems ->
                    if(newLsItems.size == 0){
                        _setonStateDeleteItem(false)
                    }
                }
            }
        }

        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemShareClickListener {
           item, _ -> sharePhoneCloud(item)
        }

        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemShareClickListener {
            item, _ -> sharePhoneCloud(item)
        }

        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnDismissClickListener {
            (rcvBlockPhone.adapter as BlockPhoneAdapter).set_isShow_Ask_Permission(false)
        }
        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnGivePhoneDefaultNowClickListener {
            (activity as MainActivity).setPermissionV2 {  }
        }
        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnGiveBlockDefaultNowClickListener {
           AppSettingsManager.setDefaultAppBlockCall(activity)
        }
    }
    private fun _setonStateDeleteItem(bool: Boolean){
        if(bool){
            imgAdd.visibility = View.INVISIBLE
            imgMenu.visibility = View.INVISIBLE
            tvCancel.visibility = View.VISIBLE
        }else{
            imgAdd.visibility = View.VISIBLE
            imgMenu.visibility = View.VISIBLE
            tvCancel.visibility = View.INVISIBLE
        }
    }

    private fun refreshRCV(){
        (rcvBlockPhone.adapter as BlockPhoneAdapter).refresh(CallPhone.getAllDB(context))
    }

    private fun menuClickSelectAll(){
        (rcvBlockPhone.adapter as BlockPhoneAdapter)
                .refresh(CallPhone.updateAllDB__Status_Block(context))
    }

    private fun menuClickIgnoreAll(){
        (rcvBlockPhone.adapter as BlockPhoneAdapter)
            .refresh(CallPhone.updateAllDB__Status_UNBLOCK(context))
    }
    private fun menuClickDeleteAll(){
        alert(message = getString(R.string.are_you_delete), cancelable = true, cancel = true){
            (rcvBlockPhone.adapter as BlockPhoneAdapter)
                    .refresh(CallPhone.deleteAllDB(context))
        }

    }


    private fun showOptionsMenu(view: View, onclickItem: PopupWindowMenu.Itf){
       if(popupMenu == null){
           lsTitle =  listOf("Select all", "Ignore all", "Delete all")
           lsIcons =listOf(
                   ContextCompat.getDrawable(context!!, R.drawable.ic_selection_all),
                   ContextCompat.getDrawable(context!!, R.drawable.ic_round_not_select),
                   ContextCompat.getDrawable(context!!, R.drawable.ic_delete_all))
           popupMenu = PopupWindowMenu(
                   context, lsTitle, lsIcons,
                   ContextCompat.getColor(context!!, R.color.background_item),
                   onclickItem
           )
       }

        popupMenu?.showAsDropDown(view, 0, -70)
    }


    //////////////
    @SuppressLint("HardwareIds")
    private fun sharePhoneCloud(item: CallPhone) {
        val android_id = Settings.Secure.getString(context?.contentResolver, ANDROID_ID)
        val body = AddPhoneCloud()
        body.phone = item.phone
        body.name = item.name
        body.type = if (item.type == null) {
            CallPhoneKEY.TYPE.TYPE_NORMAL
        } else {
            item.type
        }
        body.user_id = android_id

        API.addPhoneCloud(body, object : API.ApiItf<BlockPhoneRes> {
            override fun onSuccess(response: ServiceResult<BlockPhoneRes>?) {
                if(response?.data != null){
                    alert(response.message, response.data.note, false){}
                }else{
                    alert(response?.message?:"", response?.message?:getString(R.string.successfully), false){}
                }
            }

            override fun onError(message: String?) {
                alert(getString(R.string.error), message!!, false){}

            }

        })
    }
}