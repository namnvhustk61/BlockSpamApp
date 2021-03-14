package com.stork.viewcustom.popup

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu

class ShowPopupMenu {
     fun showPickMenu(context: Context, viewLatch: View, menuRes: Int){
        val popupMenu = PopupMenu(context, viewLatch)
        popupMenu.inflate(menuRes)
        popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
            false
        }
        popupMenu.show()
    }
}