package com.stork.blockspam.ui.fragrecentcall

import android.content.Context
import android.graphics.drawable.Drawable
import android.provider.CallLog
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stork.blockspam.R
import com.stork.blockspam.extension.areMultipleSIMsAvailable
import com.stork.blockspam.extension.beVisibleIf
import com.stork.blockspam.extension.formatDateOrTime
import com.stork.blockspam.extension.getFormattedDuration
import kotlinx.android.synthetic.main.item_recent_call.view.*

class RecentCallAdapter : RecyclerView.Adapter<ViewHolder>() {
    private val _items: MutableList<RecentCall> = mutableListOf()
    fun get_items(): MutableList<RecentCall>{
        return _items
    }

    private val VIEWTYPEDATA = 1
    private val VIEWTYPENULL = 0

    private var view:View? = null

    override fun getItemViewType(position: Int): Int {
       return if(_items.size > 0){
           VIEWTYPEDATA
       }else{
           VIEWTYPENULL
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType){
            VIEWTYPEDATA->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_call, parent, false)
                ThisViewHolder(view!!)
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

    override fun getItemCount(): Int {
        return if(_items.size >0){
            _items.size
        }else{
            1
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder){
            is ThisViewHolder->{
                holder.setData(_items[position])

            }
        }
    }

    fun refresh(items: List<RecentCall>?) {
        if (items == null){return}
        this._items.clear()
        this._items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun append(items: List<RecentCall>?){
        if (items == null){return}
        this._items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun setConfig(context: Context){
        areMultipleSIMsAvailable = context.areMultipleSIMsAvailable()
    }
     var areMultipleSIMsAvailable: Boolean = false

    inner class ThisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val  outgoingCallIcon: Drawable? = ContextCompat.getDrawable(itemView.context, R.drawable.ic_call_decline)
        private val  incomingCallIcon: Drawable? = ContextCompat.getDrawable(itemView.context, R.drawable.ic_phone)
        fun setData(call: RecentCall){
            itemView.apply {
                var nameToShow = SpannableString(call.name)
                if (call.neighbourIDs.isNotEmpty()) {
                    nameToShow = SpannableString("$nameToShow (${call.neighbourIDs.size + 1})")
                }

                item_recents_name.apply {
                    text = nameToShow
                }

                item_recents_date_time.apply {
                    text = call.startTS.formatDateOrTime()
                }

                item_recents_duration.apply {
                    text =call.duration.getFormattedDuration()
                }

                item_recents_sim_image.beVisibleIf(areMultipleSIMsAvailable)
                item_recents_sim_id.beVisibleIf(areMultipleSIMsAvailable)
                if (areMultipleSIMsAvailable) {
                    item_recents_sim_id.text = call.simID.toString()
                }

//                item_recents_image
//                call.photoUri
                val drawable = when (call.type) {
                    CallLog.Calls.OUTGOING_TYPE -> outgoingCallIcon
                    CallLog.Calls.MISSED_TYPE -> incomingCallIcon
                    else -> incomingCallIcon
                }

                item_recents_type.setImageDrawable(drawable)
            }
        }
        fun setOnEvent(){}
    }
    class ViewNull(itemView: View): RecyclerView.ViewHolder(itemView){

    }
}