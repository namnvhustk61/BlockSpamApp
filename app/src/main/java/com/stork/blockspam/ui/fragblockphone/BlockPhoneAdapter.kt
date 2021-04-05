package com.stork.blockspam.ui.fragblockphone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stork.blockspam.R
import com.stork.blockspam.database.model.CallPhone.CallPhone
import com.stork.blockspam.database.model.CallPhone.CallPhoneKEY
import com.stork.viewcustom.general.ImageViewSwap
import com.stork.viewcustom.otherlayout.MySwipeLayout
import com.stork.viewcustom.otherlayout.MySwipeLayout.DragEdge.*
import com.stork.viewcustom.otherlayout.MySwipeLayout.ShowMode.*
import com.stork.viewcustom.radius.ImageViewRadius
import kotlinx.android.synthetic.main.item_block_phone.view.*
import kotlinx.android.synthetic.main.item_block_phone_swipe.view.*
import kotlinx.android.synthetic.main.layout_ask_permission.view.*

class BlockPhoneAdapter : RecyclerView.Adapter<ViewHolder>() {
    val items = mutableListOf<CallPhone>()
    private val VIEWTYPEDATA = 1
    private val VIEWTYPENULL = 0
    private val VIEWTYPEASKPERMISSION = 2

    var onStateDeleteItem: Boolean = false
    private var view:View? = null

    private var isShow_Ask_Permission: Boolean = false

    override fun getItemViewType(position: Int): Int {
        if(position ==0 && isShow_Ask_Permission) return VIEWTYPEASKPERMISSION
        return if (items.size != 0){
             VIEWTYPEDATA
        }else{
            VIEWTYPENULL
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType){
            VIEWTYPEDATA->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_block_phone_swipe, parent, false)
                ThisViewHolder(view!!)
            }
            VIEWTYPEASKPERMISSION->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_ask_permission, parent, false)
                ViewAskPermission(view!!)
            }
            VIEWTYPENULL->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_null, parent, false)
                ViewNull(view!!)
            }
            else  -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_block_phone_swipe, parent, false)
                ThisViewHolder(view!!)
            }
        }
    }

    var _numbItemPlus = 0;
    override fun getItemCount(): Int {
        _numbItemPlus = if(this.isShow_Ask_Permission){1}else{0}
        val itemCount = _numbItemPlus + if (items.size != 0){
            items.size
        }else{
            1
        }
        return itemCount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder){
            is ThisViewHolder->{
                val posInData = position - _numbItemPlus
                holder.setData(items[posInData], this.onStateDeleteItem)
                holder.setOnEvent(
                        this,
                        items[posInData], posInData
                )
            }
            is ViewAskPermission->{
                holder.setOnEvent(this)
            }
        }
    }

    fun refresh(items: List<CallPhone>?) {
        if (items == null){return}
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun append(items: List<CallPhone>?){
        if (items == null){return}
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun setonStateDeleteItem(on: Boolean){
        this.onStateDeleteItem = on
        this.notifyDataSetChanged()
    }

    private var onItemClickListener: ((item: CallPhone)->Unit)? = null

    fun setOnItemClickListener(onItemClickListener: ((item: CallPhone)->Unit)){
        this.onItemClickListener = onItemClickListener
    }

    private var onItemLongClickListener: ((item: CallPhone)->Unit)? = null

    fun setOnItemLongClickListener(onItemLongClickListener: ((item: CallPhone)->Unit)){
        this.onItemLongClickListener = onItemLongClickListener
    }

    private var onItemDeleteClickListener: ((item: CallPhone, index: Int)->Unit)? = null

    fun setOnItemDeleteClickListener(onItemDeleteClickListener: ((item: CallPhone, index: Int)->Unit)){
        this.onItemDeleteClickListener = onItemDeleteClickListener
    }

    private var onItemShareClickListener: ((item: CallPhone, index: Int)->Unit)? = null

    fun setOnItemShareClickListener(onItemShareClickListener: ((item: CallPhone, index: Int)->Unit)){
        this.onItemShareClickListener = onItemShareClickListener
    }

    ////////// on item VIEWTYPEASKPERMISSION
    fun set_isShow_Ask_Permission(bool: Boolean){
        this.isShow_Ask_Permission = bool
        notifyDataSetChanged()
    }

    private var onGiveNowClickListener: (()->Unit)? = null
    fun setOnGiveNowClickListener(onGiveNowClickListener: (()->Unit)){
        this.onGiveNowClickListener = onGiveNowClickListener
    }

    private var onDismissClickListener: (()->Unit)? = null
    fun setOnDismissClickListener(onDismissClickListener: (()->Unit)){
        this.onDismissClickListener = onDismissClickListener
    }

    class ThisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPhone: TextView = itemView.tvPhone
        private val tvName: TextView = itemView.tvName
        private val imgSwCheck: ImageViewSwap = itemView.imgSwCheck
        private val imgStatus: ImageViewRadius = itemView.imgStatus

        private val MySwipeLayout: MySwipeLayout = itemView.swipeLayout
        private val bottom_wrapper_right: ImageViewRadius = itemView.bottom_wrapper_right
        private val bottom_wrapper_left: ImageViewRadius = itemView.bottom_wrapper_left

        fun setData(item: CallPhone, onStateDeleteItem: Boolean){
            tvPhone.text = item.phone
            tvName.text = item.name
            if(onStateDeleteItem){
                imgSwCheck.setImageResource(R.drawable.ic_delete_item)
                return
            }
            if(item.status == CallPhoneKEY.STATUS.STATUS_BLOCK){
                imgSwCheck.setActive(true)
                imgStatus.setImageResource(R.drawable.ic_protect_good)
            }else{
                imgSwCheck.setActive(false)
                imgStatus.setImageResource(R.drawable.ic_protect_fail)
            }

            // Set Swipe
            MySwipeLayout.setShowMode(PullOut)
            MySwipeLayout.isClickToClose = true
            // Drag From Left
            MySwipeLayout.addDrag(
                Left,
                MySwipeLayout.findViewById(R.id.bottom_wrapper_left)
            )
            // Drag From Right
            MySwipeLayout.addDrag(
                Right,
                MySwipeLayout.findViewById(R.id.bottom_wrapper_right)
            )
            // Handling different events when swiping
            MySwipeLayout.addSwipeListener(object : MySwipeLayout.SwipeListener {
                override fun onClose(layout: MySwipeLayout) {
                    //when the SurfaceView totally cover the BottomView.
                }
                override fun onUpdate(
                    layout: MySwipeLayout,
                    leftOffset: Int,
                    topOffset: Int
                ) {
                    //you are swiping.
                }
                override fun onStartOpen(layout: MySwipeLayout) {}
                override fun onOpen(layout: MySwipeLayout) {
                    //when the BottomView totally show.
                }

                override fun onStartClose(layout: MySwipeLayout) {}
                override fun onHandRelease(
                    layout: MySwipeLayout,
                    xvel: Float,
                    yvel: Float
                ) {
                    //when user's hand released.
                }
            })

        }

        fun setOnEvent(
                adapter: BlockPhoneAdapter,
                item: CallPhone, position: Int
        ){

            if(adapter.onItemDeleteClickListener != null && adapter.onStateDeleteItem){
                imgSwCheck.setOnClickListener {
                    adapter.onItemDeleteClickListener?.invoke(item, position)
                    adapter.notifyItemChanged(position)
                }
            }

            if(adapter.onItemLongClickListener != null){
                MySwipeLayout.surfaceView.setOnLongClickListener{v ->
                    adapter.onItemLongClickListener?.invoke(item)
                    adapter.notifyDataSetChanged()
                    true
                }
            }

            if(adapter.onItemClickListener != null && !adapter.onStateDeleteItem){
                MySwipeLayout.surfaceView.setOnClickListener {
                    adapter.onItemClickListener?.invoke(item)
                    adapter.notifyItemChanged(position)
                }
            }

            if(adapter.onItemShareClickListener != null){
                bottom_wrapper_right.setOnClickListener {
                    adapter.onItemShareClickListener?.invoke(item, position)
                    adapter.notifyItemChanged(position)
                }
                bottom_wrapper_left.setOnClickListener {
                    adapter.onItemShareClickListener?.invoke(item, position)
                    adapter.notifyItemChanged(position)
                }
            }

        }
    }
    class ViewNull(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    class ViewAskPermission(itemView: View): RecyclerView.ViewHolder(itemView){
        private val tvGiveNow: TextView = itemView.tvGiveNow
        private val tvDismiss: TextView = itemView.tvDismiss

        fun setOnEvent(adapter: BlockPhoneAdapter){
            tvGiveNow.setOnClickListener {
                adapter.onGiveNowClickListener?.invoke()
            }
            tvDismiss.setOnClickListener {
                adapter.onDismissClickListener?.invoke()
            }
        }

    }



}