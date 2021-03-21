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
import com.stork.http.API
import com.stork.http.ServiceResult
import com.stork.http.model.AddPhoneCloud
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
            imgAdd.visibility = View.VISIBLE
            imgMenu.visibility = View.VISIBLE
            tvCancel.visibility = View.INVISIBLE
            (rcvBlockPhone.adapter as BlockPhoneAdapter).setonStateDeleteItem(false)
        }

        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemLongClickListener { item ->
            (rcvBlockPhone.adapter as BlockPhoneAdapter).setonStateDeleteItem(true)
            imgAdd.visibility = View.INVISIBLE
            imgMenu.visibility = View.INVISIBLE
            tvCancel.visibility = View.VISIBLE
        }

        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemClickListener {
                item -> item.updateDB__changeStatus(context)
        }

        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemDeleteClickListener { item, index ->
            if(item.deleteDb(context)){
                (rcvBlockPhone.adapter as BlockPhoneAdapter).items.removeAt(index)
                rcvBlockPhone.adapter?.notifyItemRemoved(index)
            }
        }

        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemShareClickListener {
           item, index -> sharePhoneCloud(item)
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
        (rcvBlockPhone.adapter as BlockPhoneAdapter)
            .refresh(CallPhone.deleteAllDB(context))
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

        API.addPhoneCloud(body, object : API.ApiItf<Objects> {
            override fun onSuccess(response: ServiceResult<Objects>?) {
                alert(response!!.message)
            }

            override fun onError(message: String?) {
                alert(message!!)

            }

        })
    }
}