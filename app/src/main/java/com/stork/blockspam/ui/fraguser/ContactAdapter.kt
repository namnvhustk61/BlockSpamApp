package com.stork.blockspam.ui.fraguser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stork.blockspam.R
import com.stork.blockspam.extension.get1CharHeadOfNameUpperCase
import com.stork.blockspam.extension.get2CharHeadOfName
import com.stork.blockspam.extension.getRandomBgDrawable
import com.stork.blockspam.model.PhoneContact
import com.stork.blockspam.ui.fragblockphone.BlockPhoneAdapter
import com.stork.viewcustom.general.TextViewAction
import com.stork.viewcustom.otherlayout.MySwipeLayout
import com.stork.viewcustom.radius.ImageViewRadius
import kotlinx.android.synthetic.main.item_contact_phone.view.*
import kotlinx.android.synthetic.main.item_contact_phone.view.swipeLayout

import java.util.*

class ContactAdapter : RecyclerView.Adapter<ViewHolder>() {
    val _items = mutableListOf<PhoneContact>()

    private val VIEWTYPEDATA = 1
    private val VIEWTYPENULL = 0
    private val VIEWTYPEDATATITLE = 3

    // Add Title
    private val mapTypeItem: HashMap<String, List<PhoneContact>> = hashMapOf()
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
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact_phone, parent, false)
                ThisViewHolder(view!!)
            }
            VIEWTYPENULL ->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_null, parent, false)
                ViewNull(view!!)
            }
            VIEWTYPEDATATITLE->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_call_title, parent, false)
                BlockPhoneAdapter.ViewTitleType(view!!)
            }
            else ->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact_phone, parent, false)
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
                    holder.setData(it, position)
                    holder.setOnEvent(this, it, position)
                }
            }
            is BlockPhoneAdapter.ViewTitleType ->{
                val item = getItemDataInMap(position+1)
                holder.setData(get1CharHeadOfNameUpperCase(item?.name))
            }
        }
    }

    fun refresh(items: List<PhoneContact>?) {
        if (items == null){return}

        this._items.clear()
        this.mapTypeItem.clear()
        this.lsKeyTitleType.clear()

        this._items.addAll(items)

        // TYPE_LOCAL
        this._items.forEach { _item: PhoneContact? ->
            val titleKey = get1CharHeadOfNameUpperCase(_item?.name)
            val indexType = lsKeyTitleType.indexOf(titleKey)
            if(indexType == -1){
                // create new item map
                val entryNew: ArrayList<PhoneContact>  = arrayListOf<PhoneContact>()
                val keyNew: String = "${lsKeyTitleType.size}-${titleKey}"

                entryNew.add(_item!!)
                mapTypeItem.put(keyNew, entryNew)
                // create new item ls type
                lsKeyTitleType.add(titleKey)

            }else{
                val key = "${indexType}-${titleKey}"
                val entry =  mapTypeItem.get(key) as  ArrayList<PhoneContact>

                entry.add(_item!!)
            }
        }

        this.notifyDataSetChanged()
    }

    fun getItemDataInMap(position: Int): PhoneContact?{
        val posReal = position
        var sum = 0
        var result: PhoneContact?= null
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

    private var onItemClickListener: ((item: PhoneContact)->Unit)? = null
    fun setOnItemClickListener(onItemClickListener: ((item: PhoneContact)->Unit)){
        this.onItemClickListener = onItemClickListener
    }

    private var onItemSwipeClickListener_Block: ((item: PhoneContact, index: Int)->Unit)? = null
    fun setOnItemSwipeClickListener_Block(onItemSwipeClickListener_Block: ((item: PhoneContact, index: Int)->Unit)){
        this.onItemSwipeClickListener_Block = onItemSwipeClickListener_Block
    }

    private var onItemSwipeClickListener_Call: ((item: PhoneContact, index: Int)->Unit)? = null
    fun setOnItemSwipeClickListener_Call(onItemSwipeClickListener_Call: ((item: PhoneContact, index: Int)->Unit)){
        this.onItemSwipeClickListener_Call = onItemSwipeClickListener_Call
    }

    private var onItemSwipeClickListener_Message: ((item: PhoneContact, index: Int)->Unit)? = null
    fun setOnItemSwipeClickListener_Message(onItemSwipeClickListener_Message: ((item: PhoneContact, index: Int)->Unit)){
        this.onItemSwipeClickListener_Message = onItemSwipeClickListener_Message
    }


    class ThisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPhone: TextView = itemView.tvPhone
        private val tvName: TextView = itemView.tvName
        private val tvHeader: TextViewAction = itemView.tvHeader

        private val mySwipeLayout: MySwipeLayout = itemView.swipeLayout
        private val bottom_wrapper_right_block: ImageViewRadius = itemView.bottom_wrapper_right_block
        private val bottom_wrapper_right_call: ImageViewRadius = itemView.bottom_wrapper_right_call
        private val bottom_wrapper_right_message: ImageViewRadius = itemView.bottom_wrapper_right_message



        fun setData(item: PhoneContact, pos: Int){
            if(item.name != ""){
                tvName.text = item.name
                tvPhone.text = item.phoneNumbers[0]
                tvHeader.background = ContextCompat.getDrawable(itemView.context, getRandomBgDrawable())
                tvHeader.text = get2CharHeadOfName(item.name)
            }else{
                tvName.text =  item.phoneNumbers[0]
                tvPhone.text = itemView.context.getString(R.string.unknown)
                tvHeader.text = ""
                tvHeader.background = ContextCompat.getDrawable(itemView.context, R.drawable.ic_text_view_round_0)
            }
        }



        fun setOnEvent(adapter: ContactAdapter, item: PhoneContact, pos: Int){

            if(adapter.onItemClickListener != null && !adapter.onStateLongCLick){
                mySwipeLayout.surfaceView.setOnClickListener {
                    adapter.onItemClickListener?.invoke(item)
                    adapter.notifyItemChanged(pos)
                }
            }

            if(adapter.onItemSwipeClickListener_Block != null){
                bottom_wrapper_right_block.setOnClickListener {
                    adapter.onItemSwipeClickListener_Block?.invoke(item, pos)
                    adapter.notifyItemChanged(pos)
                }
            }

            if(adapter.onItemSwipeClickListener_Call != null){
                bottom_wrapper_right_call.setOnClickListener {
                    adapter.onItemSwipeClickListener_Call?.invoke(item, pos)
                    adapter.notifyItemChanged(pos)
                }
            }

            if(adapter.onItemSwipeClickListener_Message != null){
                bottom_wrapper_right_message.setOnClickListener {
                    adapter.onItemSwipeClickListener_Message?.invoke(item, pos)
                    adapter.notifyItemChanged(pos)
                }
            }

        }

    }
    class ViewNull(itemView: View): RecyclerView.ViewHolder(itemView)


}