package com.stork.blockspam.ui.fragblockphone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stork.blockspam.R
import com.stork.blockspam.database.CallPhone
import com.stork.blockspam.database.CallPhoneKEY
import com.stork.viewcustom.general.ImageViewSwap
import com.stork.viewcustom.radius.ImageViewRadius
import kotlinx.android.synthetic.main.item_block_phone.view.*

class BlockPhoneAdapter : RecyclerView.Adapter<ViewHolder>() {
    val items = mutableListOf<CallPhone>()
    private val VIEWTYPEDATA = 1
    private val VIEWTYPENULL = 0

    var onStateDeleteItem: Boolean = false
    private var view:View? = null

    override fun getItemViewType(position: Int): Int {
        return if (items.size != 0){
            VIEWTYPEDATA
        }else{
            VIEWTYPENULL
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return if (viewType == VIEWTYPEDATA){
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_block_phone, parent, false)
            ThisViewHolder(view!!)
        }else{
            view = LayoutInflater.from(parent.context).inflate(R.layout.layout_null, parent, false)
            ViewNull(view!!)
        }
    }

    override fun getItemCount(): Int {
        return if (items.size != 0){ items.size }else{ 1 }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ThisViewHolder){
            holder.setData(items[position], this.onStateDeleteItem)
            holder.setOnEvent(
                this,
                items[position], position
            )
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

    class ThisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPhone: TextView = itemView.tvPhone
        private val tvName: TextView = itemView.tvName
        private val imgSwCheck: ImageViewSwap = itemView.imgSwCheck
        private val imgStatus: ImageViewRadius = itemView.imgStatus

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
                itemView.setOnLongClickListener{v ->
                    adapter.onItemLongClickListener?.invoke(item)
                    adapter.notifyDataSetChanged()
                    true
                }
                return
            }

            if(adapter.onItemClickListener != null && !adapter.onStateDeleteItem){
                itemView.setOnClickListener {
                    adapter.onItemClickListener?.invoke(item)
                    adapter.notifyItemChanged(position)
                }
            }


        }
    }
    class ViewNull(itemView: View): RecyclerView.ViewHolder(itemView){

    }



}