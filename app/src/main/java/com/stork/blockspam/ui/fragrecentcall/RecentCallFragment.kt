package com.stork.blockspam.ui.fragrecentcall

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.CallLog

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager

import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.blockspam.extension.beVisibleIf
import com.stork.blockspam.extension.formatDateOrTime
import com.stork.blockspam.extension.normalizeString
import com.stork.blockspam.utils.IntentAction
import com.stork.viewcustom.popup.PopupWindowMenu
import kotlinx.android.synthetic.main.fragment_recent_call.*

class RecentCallFragment:  BaseFragment() {
    private var lsTitle: List<String>? = null
    private var lsIcons: List<Drawable?>? =null
    private var popupMenu :PopupWindowMenu?= null
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

    var lsRecentCall: List<RecentCall>? = null
    private fun refresh(){
        RecentHelper(context!!).getRecentCalls(true) { recents ->
            activity?.runOnUiThread {
                lsRecentCall = recents
                (rcvRecentCall.adapter as RecentCallAdapter).refresh(lsRecentCall)
            }
        }
    }

    private fun searchKey(text: String){
        val textNormal = text.normalizeString().toLowerCase()
        val searchList = lsRecentCall?.filter { phoneContact ->
            phoneContact.name.normalizeString().toLowerCase().contains(textNormal) ||
                    phoneContact.phoneNumber.normalizeString().toLowerCase().contains(textNormal) ||
                    phoneContact.startTS.formatDateOrTime().normalizeString().toLowerCase().contains(textNormal)

        }
        (rcvRecentCall.adapter as RecentCallAdapter).refresh(searchList)
    }

    private fun filterType(type: Int){
        /*
        * Filter by Type Call
        *   CallLog.Calls.OUTGOING_TYPE -> madeCallIcon // 2 call to
            CallLog.Calls.MISSED_TYPE -> missCallIcon  // 3 miss
            CallLog.Calls.INCOMING_TYPE -> receiveCallIcon  // 1 call from
        * */
        val searchList = lsRecentCall?.filter { phoneContact ->
            type == phoneContact.type
        }
        (rcvRecentCall.adapter as RecentCallAdapter).refresh(searchList)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(){
        rcvRecentCall.layoutManager = LinearLayoutManager(context)
        rcvRecentCall.adapter = RecentCallAdapter()

        rcvRecentCall.setOnTouchListener { v, event ->
            hideKeyBoard()
            return@setOnTouchListener false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onEvent(){
        // show click menu
        imgMenu_recent.setOnClickListener {
            showOptionsMenu(it, PopupWindowMenu.Itf { index ->
                when(index){
        /*Miss*/    0->{
                        filterType(CallLog.Calls.MISSED_TYPE)
                    }
        /*Receive*/ 1->{
                        filterType(CallLog.Calls.INCOMING_TYPE)
                    }
        /*All*/     2->{
                        searchKey("")
                    }
                }
            })
        }
        /** Event Recycle**/
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

        // Event Click btn Search
        imgSearch_recent.setOnClickListener {
            changeStatusSearch(true)
        }

        // Event Click drawable EditText search
        edtSearch_recent.let {
            it.setOnTouchListener(
                View.OnTouchListener { v, event ->
                    val DRAWABLE_LEFT = 0
                    val DRAWABLE_TOP = 1
                    val DRAWABLE_RIGHT = 2
                    val DRAWABLE_BOTTOM = 3
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= it.right - it.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                        ) {
                            it.setText("")
                            return@OnTouchListener true
                        }

                        if (event.rawX <= it.compoundDrawables[DRAWABLE_LEFT].bounds
                                .width()
                        ) {
                            it.setText("")
                            changeStatusSearch(false)
                            hideKeyBoard()
                            return@OnTouchListener true
                        }
                    }
                    false
                })

            it.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyBoard()
                    return@OnEditorActionListener true
                }
                false
            })

            it.addTextChangedListener { text ->
                searchKey(text.toString())
            }
        }

    }


    private fun changeStatusSearch(isOpen: Boolean){
        edtSearch_recent.beVisibleIf(isOpen)
        rlToolBar_recent.beVisibleIf(!isOpen)
    }


    private fun showOptionsMenu(view: View, onclickItem: PopupWindowMenu.Itf){
        if(popupMenu == null){
            lsTitle =  listOf("Miss call", "Receive call", "All call")
            lsIcons =listOf(
                    ContextCompat.getDrawable(context!!, R.drawable.ic_call_missed_24),
                    ContextCompat.getDrawable(context!!, R.drawable.ic_call_received_24),
                    ContextCompat.getDrawable(context!!, R.drawable.ic_selection_all)
            )

            popupMenu = PopupWindowMenu(
                    context, lsTitle, lsIcons,
                    ContextCompat.getColor(context!!, R.color.background_item),
                    onclickItem
            )
        }

        popupMenu?.showAsDropDown(view, 0, -70)
    }

}