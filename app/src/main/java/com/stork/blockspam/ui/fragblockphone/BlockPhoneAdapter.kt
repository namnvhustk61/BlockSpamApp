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
import com.stork.blockspam.storage.AppSharedPreferences
import com.stork.http.model.BlockPhone
import com.stork.viewcustom.general.ImageViewSwap
import com.stork.viewcustom.otherlayout.MySwipeLayout
import com.stork.viewcustom.otherlayout.MySwipeLayout.DragEdge.*
import com.stork.viewcustom.otherlayout.MySwipeLayout.ShowMode.*
import com.stork.viewcustom.radius.ImageViewRadius
import kotlinx.android.synthetic.main.item_block_phone.view.*
import kotlinx.android.synthetic.main.item_block_phone_swipe.view.*
import kotlinx.android.synthetic.main.layout_ask_permission.view.*

class BlockPhoneAdapter : RecyclerView.Adapter<ViewHolder>() {
    private val _items: MutableList<CallPhone> = mutableListOf<CallPhone>()
    fun get_items(): MutableList<CallPhone>{
        return _items
    }

    private val mapTypeItem: HashMap<String, List<CallPhone>> = hashMapOf()
    private val lsKeyTitleType = mutableListOf<String>()

    private val VIEWTYPEDATA = 1
    private val VIEWTYPEDATATITLE = 3

    private val VIEWSPACE = 4

    private val VIEWTYPENULL = 0
    private val VIEWTYPEASKPERMISSION = 2

    var onStateDeleteItem: Boolean = false
    private var view:View? = null

    private var isShow_Ask_Permission: Boolean = false

    override fun getItemViewType(position: Int): Int {
        if(position == 0) {
            return if(isShow_Ask_Permission) VIEWTYPEASKPERMISSION else VIEWSPACE
        }
         if (_items.size != 0){

             if(getItemDataInMap(position) == null){
                 return VIEWTYPEDATATITLE
             }else{
                 return VIEWTYPEDATA
             }
        }else{
          return  VIEWTYPENULL
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
            VIEWTYPEDATATITLE->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_block_phone_title, parent, false)
                ViewTitleType(view!!)
            }
            VIEWSPACE->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_space, parent, false)
                ViewNull(view!!)
            }
            else  -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_block_phone_swipe, parent, false)
                ThisViewHolder(view!!)
            }
        }
    }

    override fun getItemCount(): Int {
        // 1 item first  is VIEW TYPE ASK PERMISSION
        val itemCount = 1 + if (_items.size != 0){
            mapTypeItem.keys.size + _items.size
        }else{
            1 // VIEW TYPE NULL PERMISSION
        }
        return itemCount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder){
            is ThisViewHolder->{
                val item = getItemDataInMap(position)
                if(item != null){
                    holder.setData(item, this.onStateDeleteItem)
                    holder.setOnEvent(
                            this,
                            item, position
                    )
                }

            }
            is ViewTitleType->{
                val item = getItemDataInMap(position+1)
                holder.setData(item!!.type)
            }
            is ViewAskPermission->{
                holder.setOnEvent(this)
                holder.setData()
            }
        }
    }



    fun refresh(items: List<CallPhone>?) {
        if (items == null){return}

        this._items.clear()
        this.mapTypeItem.clear()
        this.lsKeyTitleType.clear()

        this._items.addAll(items)
        // TYPE_LOCAL
        this._items.forEach { _item: CallPhone? ->
            val indexType = lsKeyTitleType.indexOf(_item?.type)
            if(indexType == -1){
                // create new item map
                val entryNew: ArrayList<CallPhone>  = arrayListOf<CallPhone>()
                val keyNew: String = "${lsKeyTitleType.size}-${_item?.type!!}"

                entryNew.add(_item)
                mapTypeItem.put(keyNew, entryNew)
                // create new item ls type
                lsKeyTitleType.add(_item.type!!)

            }else{
                val key = "${indexType}-${_item?.type!!}"
                val entry =  mapTypeItem.get(key) as  ArrayList<CallPhone>

                entry.add(_item)
            }
        }
        this.notifyDataSetChanged()
    }

    private fun refresh() {

        this.mapTypeItem.clear()
        this.lsKeyTitleType.clear()

        // TYPE_LOCAL
        this._items.forEach { _item: CallPhone? ->
            val indexType = lsKeyTitleType.indexOf(_item?.type)
            if(indexType == -1){
                // create new item map
                val entryNew: ArrayList<CallPhone>  = arrayListOf<CallPhone>()
                val keyNew: String = "${lsKeyTitleType.size}-${_item?.type!!}"

                entryNew.add(_item)
                mapTypeItem.put(keyNew, entryNew)
                // create new item ls type
                lsKeyTitleType.add(_item.type!!)

            }else{
                val key = "${indexType}-${_item?.type!!}"
                val entry =  mapTypeItem.get(key) as  ArrayList<CallPhone>

                entry.add(_item)
            }
        }
        this.notifyDataSetChanged()
    }

    fun getItemDataInMap(position: Int): CallPhone?{
        val posReal = position -1
        var sum = 0
        var result: CallPhone?= null
        lsKeyTitleType.forEachIndexed { index, type ->
            sum += 1
            sum += mapTypeItem.get("${index}-${type}")!!.size

            if(posReal < sum){

                val entry = mapTypeItem.get("${index}-${type}")
                val idxInEntryMap = entry!!.size -( sum -1 - posReal)
                if(idxInEntryMap > 0){
                    result =  entry.get(idxInEntryMap - 1)
                }
                return result
            }
        }
        return result
    }



    fun append(items: List<CallPhone>?){
        if (items == null){return}
        this._items.addAll(items)
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

    fun deleteItem(pos: Int, item: CallPhone, itf: (newLsItems:  MutableList<CallPhone>)->Unit?){
       val idx: Int = _items.indexOfFirst { _it ->
           item.phone == _it.phone
       }
        _items.removeAt(idx)
        itf.invoke(_items)

        refresh()
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

    private var onGivePhoneDefaultNowClickListener: (()->Unit)? = null
    fun setOnGivePhoneDefaultNowClickListener(onGivePhoneDefaultNowClickListener: (()->Unit)){
        this.onGivePhoneDefaultNowClickListener = onGivePhoneDefaultNowClickListener
    }

    private var onGiveBlockDefaultNowClickListener: (()->Unit)? = null
    fun setOnGiveBlockDefaultNowClickListener(onGiveBlockDefaultNowClickListener: (()->Unit)){
        this.onGiveBlockDefaultNowClickListener = onGiveBlockDefaultNowClickListener
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
        private val tvGiveDialPhoneNow: TextView = itemView.tvGiveDialPhoneNow
        private val tvGiveBlockPhoneNow: TextView = itemView.tvGiveBlockPhoneNow
        private val tvDismiss: TextView = itemView.tvDismiss
        private val tvDes: TextView = itemView.tvDes

        fun setData(){
            val isDefaultBlockApp = AppSharedPreferences.getInstance(itemView.context).getBoolean(AppSharedPreferences.KEY_PREFERRENCE.IS_DEFAULT_BLOCK_APP)
            if(isDefaultBlockApp){
                tvGiveBlockPhoneNow.visibility = View.GONE
                tvDes.text = itemView.context.getString(R.string.ask_permission_dial)
            }
        }
        fun setOnEvent(adapter: BlockPhoneAdapter){
            tvGiveDialPhoneNow.setOnClickListener {
                adapter.onGivePhoneDefaultNowClickListener?.invoke()
            }
            tvGiveBlockPhoneNow.setOnClickListener {
                adapter.onGiveBlockDefaultNowClickListener?.invoke()
            }
            tvDismiss.setOnClickListener {
                adapter.onDismissClickListener?.invoke()
            }
        }

    }

    class ViewTitleType(itemView: View): RecyclerView.ViewHolder(itemView){
        private val tvTitle: TextView = itemView.tvTitle
        fun setData(title: String){
            tvTitle.text = title
        }
    }


}