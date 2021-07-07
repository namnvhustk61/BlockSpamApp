package com.stork.blockspam.ui.fragserver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stork.blockspam.R
import com.stork.blockspam.database.model.CallPhone.CallPhoneKEY
import com.stork.blockspam.ui.fragblockphone.BlockPhoneAdapter
import com.stork.http.model.BlockPhone
import com.stork.viewcustom.general.ImageViewSwap
import com.stork.viewcustom.otherlayout.MySwipeLayout
import com.stork.viewcustom.otherlayout.MySwipeLayout.DragEdge.*
import com.stork.viewcustom.otherlayout.MySwipeLayout.ShowMode.*
import com.stork.viewcustom.radius.ImageViewRadius
import kotlinx.android.synthetic.main.item_server_phone.view.*


class ServerAdapter : RecyclerView.Adapter<ViewHolder>() {
    val _items = mutableListOf<BlockPhone>()
    private val VIEWTYPEDATA = 1
    private val VIEWTYPENULL = 0
    private val VIEWTYPEDATATITLE = 3

    private val mapTypeItem: HashMap<String, List<BlockPhone>> = hashMapOf()
    private val lsKeyTitleType = mutableListOf<String>()

    var onStateLongCLick: Boolean = false
    private var view:View? = null

    override fun getItemViewType(position: Int): Int {
        return if(_items.size > 0){
            if(getItemDataInMap(position) == null){
                VIEWTYPEDATATITLE
            }else{
                VIEWTYPEDATA
            }
        }else{
            VIEWTYPENULL
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when(viewType){
            VIEWTYPEDATA->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_server_phone, parent, false)
                ThisViewHolder(view!!)
            }
            VIEWTYPENULL->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_null, parent, false)
                ViewNull(view!!)
            }
            VIEWTYPEDATATITLE->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_block_phone_title, parent, false)
                BlockPhoneAdapter.ViewTitleType(view!!)
            }
            else->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_server_phone, parent, false)
                ThisViewHolder(view!!)
            }
        }
    }

    override fun getItemCount(): Int {
        //
        return if (_items.size != 0){
            mapTypeItem.keys.size + _items.size
        }else{
            1 // VIEW TYPE NULL PERMISSION
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder){
            is ThisViewHolder ->{
                getItemDataInMap(position)?.let {
                    holder.setData(it,this.onStateLongCLick)
                    holder.setOnEvent(this, it, position)
                }
            }

            is BlockPhoneAdapter.ViewTitleType ->{
                val item = getItemDataInMap(position+1)
                holder.setData(item!!.type)
            }
        }
    }

    fun refresh(items: List<BlockPhone>?) {
        if (items == null){return}

        this._items.clear()
        this.mapTypeItem.clear()
        this.lsKeyTitleType.clear()

        this._items.addAll(items)

        // TYPE_LOCAL
        this._items.forEach { _item: BlockPhone? ->
            val titleKey = _item?.type?:""
            val indexType = lsKeyTitleType.indexOf(titleKey)
            if(indexType == -1){
                // create new item map
                val entryNew: ArrayList<BlockPhone>  = arrayListOf<BlockPhone>()
                val keyNew: String = "${lsKeyTitleType.size}-${titleKey}"

                entryNew.add(_item!!)
                mapTypeItem.put(keyNew, entryNew)
                // create new item ls type
                lsKeyTitleType.add(titleKey)

            }else{
                val key = "${indexType}-${titleKey}"
                val entry =  mapTypeItem.get(key) as  ArrayList<BlockPhone>

                entry.add(_item!!)
            }
        }
        this.notifyDataSetChanged()
    }

    fun getItemDataInMap(position: Int): BlockPhone?{
        val posReal = position
        var sum = 0
        var result: BlockPhone?= null
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

    fun append(items: List<BlockPhone>?){
        if (items == null){return}
        this._items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun setonStateLongCLick(on: Boolean){
        this.onStateLongCLick = on
        this.notifyDataSetChanged()
    }

    private var onItemLongClickListener: ((item: BlockPhone)->Unit)? = null

    fun setOnItemLongClickListener(onItemLongClickListener: ((item: BlockPhone)->Unit)){
        this.onItemLongClickListener = onItemLongClickListener
    }

    private var onItemClickListener: ((item: BlockPhone)->Unit)? = null

    fun setOnItemClickListener(onItemClickListener: ((item: BlockPhone)->Unit)){
        this.onItemClickListener = onItemClickListener
    }

    private var onItemSwipeClickListener: ((item: BlockPhone, index: Int)->Unit)? = null

    fun setOnItemSwipeClickListener(onItemSwipeClickListener: ((item: BlockPhone, index: Int)->Unit)){
        this.onItemSwipeClickListener = onItemSwipeClickListener
    }

    class ThisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPhone: TextView = itemView.tvPhone
        private val tvName: TextView = itemView.tvName
        private val imgSwCheck: ImageViewSwap = itemView.imgSwCheck

        private val mySwipeLayout: MySwipeLayout = itemView.swipeLayout
        private val bottom_wrapper_right: ImageViewRadius = itemView.bottom_wrapper_right
        private val bottom_wrapper_left: ImageViewRadius = itemView.bottom_wrapper_left

        fun setData(item: BlockPhone, onStateDeleteItem: Boolean){
            tvPhone.text = item.phone
            tvName.text = item.name

            if(onStateDeleteItem){
                imgSwCheck.visibility = View.VISIBLE
            }else{
                imgSwCheck.visibility = View.INVISIBLE
            }

            if(item.status == CallPhoneKEY.STATUS.STATUS_BLOCK){
                imgSwCheck.setActive(true)
            }else{
                imgSwCheck.setActive(false)
            }

            // Set Swipe
            mySwipeLayout.showMode = PullOut
            mySwipeLayout.isClickToClose = true
            // Drag From Left
            mySwipeLayout.addDrag(
                    Left,
                    mySwipeLayout.findViewById(R.id.bottom_wrapper_left)
            )
            // Drag From Right
            mySwipeLayout.addDrag(
                    Right,
                    mySwipeLayout.findViewById(R.id.bottom_wrapper_right)
            )

        }

        fun setOnEvent(
            adapter: ServerAdapter,
            item: BlockPhone, position: Int
        ){

            if(adapter.onItemLongClickListener != null){
                mySwipeLayout.surfaceView.setOnLongClickListener{v ->
                    adapter.onItemLongClickListener?.invoke(item)
                    adapter.notifyDataSetChanged()
                    true
                }
            }

            if(adapter.onItemClickListener != null && adapter.onStateLongCLick){
                mySwipeLayout.surfaceView.setOnClickListener {
                    adapter.onItemClickListener?.invoke(item)
                    adapter.notifyItemChanged(position)
                }
            }

            if(adapter.onItemSwipeClickListener != null){
                bottom_wrapper_right.setOnClickListener {
                    adapter.onItemSwipeClickListener?.invoke(item, position)
                    adapter.notifyItemChanged(position)
                }
                bottom_wrapper_left.setOnClickListener {
                    adapter.onItemSwipeClickListener?.invoke(item, position)
                    adapter.notifyItemChanged(position)
                }
            }
        }
    }
    class ViewNull(itemView: View): RecyclerView.ViewHolder(itemView)


}