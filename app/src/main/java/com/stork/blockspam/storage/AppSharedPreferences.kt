package com.stork.blockspam.storage

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreferences {

    companion object{
        private const val appName = "*Stork*"
        private var instance: AppSharedPreferences? = null
        private var sharedPreferences: SharedPreferences? = null


        fun getInstance(context: Context): AppSharedPreferences {
            if (instance == null) {
               instance = AppSharedPreferences()
            }
            if(sharedPreferences == null){
                sharedPreferences = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            }
            return instance as AppSharedPreferences
        }
    }
////////////////
    enum class KEY_PREFERRENCE{
        TAB_SELECTED, SERVICE_RUNNING
    }

    final val vALUE_STR_FAIL: String = "*$*"
    final val vALUE_INT_FAIL: Int = Int.MIN_VALUE

    fun saveString( key :KEY_PREFERRENCE, value: String) {
        sharedPreferences?.edit()?.putString(key.name, value)?.apply()
    }

    fun getString(key: KEY_PREFERRENCE): String? {
        if(sharedPreferences == null){return vALUE_STR_FAIL}
        return sharedPreferences!!.getString( key.name, vALUE_STR_FAIL)
    }

    fun saveInt( key :KEY_PREFERRENCE, value: Int) {
        sharedPreferences?.edit()?.putInt(key.name, value)?.apply()
    }

    fun getInt(key: KEY_PREFERRENCE): Int? {
        if(sharedPreferences == null){return vALUE_INT_FAIL}
        return sharedPreferences!!.getInt( key.name, vALUE_INT_FAIL)
    }

    fun removeKey(key: KEY_PREFERRENCE){
        sharedPreferences?.edit()?.remove(key.name)?.apply()
    }
}