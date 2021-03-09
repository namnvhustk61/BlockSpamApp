package com.stork.blockspam.ui.fragblockphone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stork.blockspam.R

class BlockPhoneAdapter : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
          val view:View? = LayoutInflater.from(parent.context).inflate(R.layout.item_block_phone, parent, false)
        return ViewNull(view!!)
    }

    override fun getItemCount(): Int {
        return 20;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    class ViewNull(itemView: View): RecyclerView.ViewHolder(itemView){

    }


}