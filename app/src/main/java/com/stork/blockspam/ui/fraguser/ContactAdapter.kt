package com.stork.blockspam.ui.fraguser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stork.blockspam.R
import com.stork.blockspam.model.PhoneContact
import com.stork.viewcustom.general.TextViewAction
import com.stork.viewcustom.otherlayout.MySwipeLayout
import com.stork.viewcustom.radius.ImageViewRadius
import kotlinx.android.synthetic.main.item_contact_phone.view.*
import kotlinx.android.synthetic.main.item_contact_phone.view.swipeLayout

import java.util.*

class ContactAdapter<T> : RecyclerView.Adapter<ViewHolder>() {
    val items = mutableListOf<T>()
    private val VIEWTYPEDATA = 1
    private val VIEWTYPENULL = 0

    var onStateLongCLick: Boolean = false
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
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact_phone, parent, false)
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
            holder.setData(items[position] as PhoneContact, position)
            holder.setOnEvent(this, items[position] as PhoneContact, position)
        }
    }

    fun refresh(items: List<T>?) {
        if (items == null){return}
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
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

        private val lsBgDrawable = listOf(
                R.drawable.ic_text_view_round_1,
                R.drawable.ic_text_view_round_2,
                R.drawable.ic_text_view_round_3,
                R.drawable.ic_text_view_round_4,
                R.drawable.ic_text_view_round_5
        )
        var random: Random = Random()
        private fun getRandomBgDrawable(): Int{
            return lsBgDrawable[random.nextInt(lsBgDrawable.size)]
        }
        fun setData(item: PhoneContact, pos: Int){
            if(item.name != ""){
                tvName.text = item.name
                tvPhone.text = item.phoneNumbers[0]
                tvHeader.background = ContextCompat.getDrawable(itemView.context, getRandomBgDrawable())
                tvHeader.text = get2CharHeadOfName(item.name)
            }else{
                tvName.text =  item.phoneNumbers[0]
                tvPhone.text = "#"
                tvHeader.text = "#"
                tvHeader.background = ContextCompat.getDrawable(itemView.context, R.drawable.ic_text_view_round_0)
            }
        }

        private fun get2CharHeadOfName(name: String): String{
            val ls = name.trim().split(" ")
            if(ls.size == 1 && ls[0].length >= 2){
                return ls[0].substring(0, 2)
            }
            var value = ""
            ls.forEach { str: String ->
            }
            for ( index in ls.indices){
                if(ls[index].isNotEmpty()){
                    value += (ls[index][0])
                }
                if(value.length == 2){break}
            }
            return value

        }

        fun setOnEvent(adapter: ContactAdapter<*>, item: PhoneContact, pos: Int){

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