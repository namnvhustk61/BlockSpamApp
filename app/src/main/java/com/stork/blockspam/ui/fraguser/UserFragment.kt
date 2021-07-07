package com.stork.blockspam.ui.fraguser

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseFragment
import com.stork.blockspam.extension.beVisibleIf
import com.stork.blockspam.extension.normalizeString
import com.stork.blockspam.model.PhoneContact
import com.stork.blockspam.utils.AppPermission
import com.stork.blockspam.utils.IntentAction
import com.stork.viewcustom.popup.PopupWindowMenu
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.layout_ask_permission.*
import kotlinx.android.synthetic.main.layout_ask_permission.view.*
import java.util.*


class UserFragment : BaseFragment() {

    private var lsTitle: List<String>? = null
    private var lsIcons: List<Drawable?>? =null
    private var popupMenu :PopupWindowMenu?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        onEvent()

    }

    override fun onResume() {
        super.onResume()
        showAskLayout(!PhoneContact.checkPermission(context!!))

        context?.let {
            if(PhoneContact.checkPermission(it)){
                setDataRCV()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AppPermission.onRequestPermissionsResult(requestCode, permissions, grantResults) { state: Boolean ->
          if(state){
              showAskLayout(false)
              setDataRCV()
          }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        tvDismiss.setOnClickListener { showAskLayout(false)}
        tvGiveDialPhoneNow.setOnClickListener {  PhoneContact.requirePermissions(this) }

        rcvContacts.layoutManager =  LinearLayoutManager(context)
        rcvContacts.adapter =  ContactAdapter()

        rcvContacts.setOnTouchListener { v, event ->
            hideKeyBoard()
            return@setOnTouchListener false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onEvent(){

        // show click menu
        imgMenu_user.setOnClickListener {
            showOptionsMenu(it, PopupWindowMenu.Itf { index ->
                when(index){
                    0->{IntentAction.createNewContract(context!!)}
                }
            })
        }
        /** Event Recycle**/
        (rcvContacts.adapter as ContactAdapter).setOnItemClickListener { item->
            IntentAction.callPhone(activity as AppCompatActivity, item.phoneNumbers[0])
        }

        (rcvContacts.adapter as ContactAdapter).setOnItemSwipeClickListener_Block { item, index ->
            blockPhone(item.phoneNumbers[0], item.name)
        }

        (rcvContacts.adapter as ContactAdapter).setOnItemSwipeClickListener_Call{ item, index ->
            IntentAction.callPhone(activity as AppCompatActivity, item.phoneNumbers[0])
        }

        (rcvContacts.adapter as ContactAdapter).setOnItemSwipeClickListener_Message { item, index ->
            IntentAction.sendSMS(context!!, item.phoneNumbers[0])
        }

        // Event Click btn Search
        imgSearch_user.setOnClickListener {
            changeStatusSearch(true)
        }

        // Event Click drawable EditText search
        edtSearch_user.setOnTouchListener(
            OnTouchListener { v, event ->
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= edtSearch_user.right - edtSearch_user.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                    ) {
                        edtSearch_user.setText("")
                        return@OnTouchListener true
                    }

                    if (event.rawX <= edtSearch_user.compoundDrawables[DRAWABLE_LEFT].bounds
                            .width()
                    ) {
                        edtSearch_user.setText("")
                        changeStatusSearch(false)
                        hideKeyBoard()
                        return@OnTouchListener true
                    }
                }
                false
            })

        edtSearch_user.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyBoard()
                return@OnEditorActionListener true
            }
            false
        })

        edtSearch_user.addTextChangedListener { text ->
            searchKey(text.toString())
        }
    }

    private fun showAskLayout(bool: Boolean){
        if (bool){
            loAskPer.visibility = View.VISIBLE
        }else{
            loAskPer.visibility = View.GONE
        }
    }

    var listPhoneContact: List<PhoneContact>? = null
    private fun setDataRCV(){
        listPhoneContact = PhoneContact.getContacts(
            context!!,
            false
        )
        (rcvContacts.adapter as? ContactAdapter)?.refresh(listPhoneContact)
    }

    private fun searchKey(text: String){
        val textNormal = text.normalizeString().toLowerCase()
        val searchList = listPhoneContact?.filter { phoneContact ->
            phoneContact.name.normalizeString().toLowerCase().contains(textNormal) ||
            phoneContact.phoneNumbers[0].normalizeString().toLowerCase().contains(textNormal)

        }

        (rcvContacts.adapter as? ContactAdapter)?.refresh(searchList)
    }

    private fun changeStatusSearch(isOpen: Boolean){
        edtSearch_user.beVisibleIf(isOpen)
        llActionBar_user.beVisibleIf(!isOpen)
    }

    /**
     * Show Menu
     * */

    private fun showOptionsMenu(view: View, onclickItem: PopupWindowMenu.Itf){
        if(popupMenu == null){
            lsTitle =  listOf("Add new")
            lsIcons =listOf(
                    ContextCompat.getDrawable(context!!, R.drawable.ic_add))

            popupMenu = PopupWindowMenu(
                    context, lsTitle, lsIcons,
                    ContextCompat.getColor(context!!, R.color.background_item),
                    onclickItem
            )
        }

        popupMenu?.showAsDropDown(view, 0, -70)
    }
}