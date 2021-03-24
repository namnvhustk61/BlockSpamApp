package com.stork.blockspam.ui.fragserver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stork.blockspam.R
import com.stork.blockspam.database.model.CallPhone.CallPhoneKEY
import com.stork.http.model.BlockPhone
import com.stork.viewcustom.general.ImageViewSwap
import com.stork.viewcustom.otherlayout.MySwipeLayout
import com.stork.viewcustom.otherlayout.MySwipeLayout.DragEdge.*
import com.stork.viewcustom.otherlayout.MySwipeLayout.ShowMode.*
import com.stork.viewcustom.radius.ImageViewRadius
import kotlinx.android.synthetic.main.item_server_phone.view.*


class ServerAdapter : RecyclerView.Adapter<ViewHolder>() {
    val items = mutableListOf<BlockPhone>()
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
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_server_phone, parent, false)
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
            holder.setData(items[position], this.onStateLongCLick)
            holder.setOnEvent(
                this,
                items[position], position
            )
        }
    }

    fun refresh(items: List<BlockPhone>?) {
        if (items == null){return}
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun append(items: List<BlockPhone>?){
        if (items == null){return}
        this.items.addAll(items)
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

        private val MySwipeLayout: MySwipeLayout = itemView.swipeLayout
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
            MySwipeLayout.showMode = PullOut
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

        }

        fun setOnEvent(
            adapter: ServerAdapter,
            item: BlockPhone, position: Int
        ){

            if(adapter.onItemLongClickListener != null){
                MySwipeLayout.surfaceView.setOnLongClickListener{v ->
                    adapter.onItemLongClickListener?.invoke(item)
                    adapter.notifyDataSetChanged()
                    true
                }
            }

            if(adapter.onItemClickListener != null && adapter.onStateLongCLick){
                MySwipeLayout.surfaceView.setOnClickListener {
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