package com.stork.blockspam.storage

import android.content.Context

class Constant {
    companion object{
        private var instance: Constant? = null
        fun getInstance(): Constant {
            if (instance == null) {
                instance = Constant()
            }
            return instance as Constant
        }
    }

    var selected_tab_home = 0
}