package com.stork.blockspam.ui.fragblockphone

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.blockspam.database.CallPhone
import com.stork.blockspam.navigation.AppNavigation
import com.stork.viewcustom.popup.PopupWindowMenu
import kotlinx.android.synthetic.main.fragment_block_phone.*


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
        (rcvBlockPhone.adapter as BlockPhoneAdapter).setOnItemClickListener {
            item -> item.updateDB__changeStatus(context)
        }
        rcvBlockPhone.layoutManager = LinearLayoutManager(context)
    }
    @SuppressLint("ResourceAsColor")
    private fun onEvent(){
        imgAdd.setOnClickListener { AppNavigation.toAddCallPhone(context!!)}
        setPopUpWindow()
        imgMenu.setOnClickListener {
            showOptionsMenu(it, PopupWindowMenu.Itf { index ->{

            }})
         }
    }

    private fun refreshRCV(){
        (rcvBlockPhone.adapter as BlockPhoneAdapter).refresh(CallPhone.getAllDB(context))
    }


    var mypopupWindow: PopupWindow? = null
    private fun setPopUpWindow() {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_block_phone, null)
        mypopupWindow = PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
        mypopupWindow?.elevation = 60f

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

}