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
import com.stork.blockspam.extension.*
import com.stork.blockspam.ui.fragblockphone.BlockPhoneAdapter
import com.stork.viewcustom.general.TextViewAction
import com.stork.viewcustom.otherlayout.MySwipeLayout
import com.stork.viewcustom.radius.ImageViewRadius
import kotlinx.android.synthetic.main.item_recent_call.view.*
import kotlinx.android.synthetic.main.item_recent_call.view.bottom_wrapper_right_block
import kotlinx.android.synthetic.main.item_recent_call.view.bottom_wrapper_right_call
import kotlinx.android.synthetic.main.item_recent_call.view.bottom_wrapper_right_message
import kotlinx.android.synthetic.main.item_recent_call.view.swipeLayout

class RecentCallAdapter : RecyclerView.Adapter<ViewHolder>() {
    private val _items: MutableList<RecentCall> = mutableListOf()
    fun get_items(): MutableList<RecentCall>{
        return _items
    }

    // Add Title
    private val mapTypeItem: HashMap<String, List<RecentCall>> = hashMapOf()
    private val lsKeyTitleType = mutableListOf<String>()

    var onStateLongCLick: Boolean = false

    private val VIEWTYPEDATA = 1
    private val VIEWTYPENULL = 0

    private val VIEWTYPEDATATITLE = 3

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
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_call, parent, false)
                ThisViewHolder(view!!)
            }

            VIEWTYPENULL->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_null, parent, false)
                ViewNull(view!!)
            }

            VIEWTYPEDATATITLE->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_call_title, parent, false)
                BlockPhoneAdapter.ViewTitleType(view!!)
            }
            else  -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_block_phone_swipe, parent, false)
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
            is ThisViewHolder->{
               getItemDataInMap(position)?.let {
                   holder.setData(it)
                   holder.setOnEvent(this, it, position)
               }
            }

            is BlockPhoneAdapter.ViewTitleType ->{
                val item = getItemDataInMap(position+1)
                holder.setData(item?.startTS?.formatDateOrTime()?:"")
            }
        }
    }

    fun refresh(items: List<RecentCall>?) {
        if (items == null){return}

        this._items.clear()
        this.mapTypeItem.clear()
        this.lsKeyTitleType.clear()

        this._items.addAll(items)

        // TYPE_LOCAL
        this._items.forEach { _item: RecentCall? ->
            val titleKey = _item?.startTS?.formatDateOrTime()?:""
            val indexType = lsKeyTitleType.indexOf(titleKey)
            if(indexType == -1){
                // create new item map
                val entryNew: ArrayList<RecentCall>  = arrayListOf<RecentCall>()
                val keyNew: String = "${lsKeyTitleType.size}-${titleKey}"

                entryNew.add(_item!!)
                mapTypeItem.put(keyNew, entryNew)
                // create new item ls type
                lsKeyTitleType.add(titleKey)

            }else{
                val key = "${indexType}-${titleKey}"
                val entry =  mapTypeItem.get(key) as  ArrayList<RecentCall>

                entry.add(_item!!)
            }
        }
        this.notifyDataSetChanged()
    }

    fun append(items: List<RecentCall>?){
        if (items == null){return}
        this._items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun getItemDataInMap(position: Int): RecentCall?{
        val posReal = position
        var sum = 0
        var result: RecentCall?= null
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

    private var onItemClickListener: ((item: RecentCall)->Unit)? = null
    fun setOnItemClickListener(onItemClickListener: ((item: RecentCall)->Unit)){
        this.onItemClickListener = onItemClickListener
    }

    private var onItemSwipeClickListener_Block: ((item: RecentCall, index: Int)->Unit)? = null
    fun setOnItemSwipeClickListener_Block(onItemSwipeClickListener_Block: ((item: RecentCall, index: Int)->Unit)){
        this.onItemSwipeClickListener_Block = onItemSwipeClickListener_Block
    }

    private var onItemSwipeClickListener_Call: ((item: RecentCall, index: Int)->Unit)? = null
    fun setOnItemSwipeClickListener_Call(onItemSwipeClickListener_Call: ((item: RecentCall, index: Int)->Unit)){
        this.onItemSwipeClickListener_Call = onItemSwipeClickListener_Call
    }

    private var onItemSwipeClickListener_Message: ((item: RecentCall, index: Int)->Unit)? = null
    fun setOnItemSwipeClickListener_Message(onItemSwipeClickListener_Message: ((item: RecentCall, index: Int)->Unit)){
        this.onItemSwipeClickListener_Message = onItemSwipeClickListener_Message
    }


    fun setConfig(context: Context){
        areMultipleSIMsAvailable = context.areMultipleSIMsAvailable()
    }
     var areMultipleSIMsAvailable: Boolean = false

    inner class ThisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mySwipeLayout: MySwipeLayout = itemView.swipeLayout
        private val bottom_wrapper_right_block: ImageViewRadius = itemView.bottom_wrapper_right_block
        private val bottom_wrapper_right_call: ImageViewRadius = itemView.bottom_wrapper_right_call
        private val bottom_wrapper_right_message: ImageViewRadius = itemView.bottom_wrapper_right_message

        private val tvHeader: TextViewAction = itemView.tv_recents_image

        private val  madeCallIcon: Drawable? = ContextCompat.getDrawable(itemView.context, R.drawable.ic_call_made_24)
        private val  receiveCallIcon: Drawable? = ContextCompat.getDrawable(itemView.context, R.drawable.ic_call_received_24)
        private val  missCallIcon: Drawable? = ContextCompat.getDrawable(itemView.context, R.drawable.ic_call_missed_24)
        fun setData(call: RecentCall){
            itemView.apply {
                var nameToShow = SpannableString(call.name)
                if (call.neighbourIDs.isNotEmpty()) {
                    nameToShow = SpannableString("$nameToShow (${call.neighbourIDs.size + 1})")
                }
                // set avatar
                if(call.name != call.phoneNumber){
                    tvHeader.background = ContextCompat.getDrawable(itemView.context, getRandomBgDrawable())
                    tvHeader.text = get2CharHeadOfName(call.name)
                }else{

                    tvHeader.text = ""
                    tvHeader.background = ContextCompat.getDrawable(itemView.context, R.drawable.ic_text_view_round_0)
                }
                // set text
                item_call_name.apply {
                    text = nameToShow
                }

                item_recents_date_time.apply {
                    text = call.startTS.formatDateOrTime()
                }

                item_recents_duration.apply {
                    visibility = if(call.duration==0) View.INVISIBLE else View.VISIBLE
                    text = call.duration.getFormattedDuration()
                }

                item_recents_sim_image.beVisibleIf(areMultipleSIMsAvailable)
                item_recents_sim_id.beVisibleIf(areMultipleSIMsAvailable)
                if (areMultipleSIMsAvailable) {
                    item_recents_sim_id.text = call.simID.toString()
                }

//                item_recents_image
//                call.photoUri
                val drawable = when (call.type) {
                    CallLog.Calls.OUTGOING_TYPE -> madeCallIcon // 2 call to       //
                    CallLog.Calls.MISSED_TYPE -> missCallIcon  // 3 miss
                    CallLog.Calls.INCOMING_TYPE -> receiveCallIcon  // 1 call from // from out to user
                    else -> receiveCallIcon
                }

                item_recents_type.setImageDrawable(drawable)
            }
        }


        fun setOnEvent(adapter: RecentCallAdapter, item: RecentCall, pos: Int){

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
    class ViewNull(itemView: View): RecyclerView.ViewHolder(itemView){

    }
}