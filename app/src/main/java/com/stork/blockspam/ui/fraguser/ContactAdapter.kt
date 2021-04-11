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
import kotlinx.android.synthetic.main.item_contact_phone.view.*
import kotlinx.android.synthetic.main.item_server_phone.view.tvName
import kotlinx.android.synthetic.main.item_server_phone.view.tvPhone
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
        }
    }

    fun refresh(items: List<T>?) {
        if (items == null){return}
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }


    class ThisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPhone: TextView = itemView.tvPhone
        private val tvName: TextView = itemView.tvName
        private val tvHeader: TextViewAction = itemView.tvHeader
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
    }
    class ViewNull(itemView: View): RecyclerView.ViewHolder(itemView)


}