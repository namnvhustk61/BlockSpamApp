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
        // int
        TAB_SELECTED,

        // Boolean
        IS_FIRST_INSTALL,
        IS_DEFAULT_BLOCK_APP,

        // String
        SERVICE_RUNNING,
        IS_PER_BLOCK,
    }

    final val VALUE_STR_FAIL: String = "*$*"
    final val VALUE_INT_FAIL: Int = Int.MIN_VALUE
    final val VALUE_BOOLEAN_FAIL: Boolean = false

    fun saveString( key :KEY_PREFERRENCE, value: String) {
        sharedPreferences?.edit()?.putString(key.name, value)?.apply()
    }

    fun getString(key: KEY_PREFERRENCE): String? {
        if(sharedPreferences == null){return VALUE_STR_FAIL}
        return sharedPreferences!!.getString( key.name, VALUE_STR_FAIL)
    }

    fun saveInt( key :KEY_PREFERRENCE, value: Int) {
        sharedPreferences?.edit()?.putInt(key.name, value)?.apply()
    }

    fun getInt(key: KEY_PREFERRENCE): Int? {
        if(sharedPreferences == null){return VALUE_INT_FAIL}
        return sharedPreferences!!.getInt( key.name, VALUE_INT_FAIL)
    }

    fun saveBoolean(key :KEY_PREFERRENCE, value: Boolean) {
        sharedPreferences?.edit()?.putBoolean(key.name, value)?.apply()
    }

    fun getBoolean(key :KEY_PREFERRENCE):Boolean {
        if(sharedPreferences == null){return VALUE_BOOLEAN_FAIL}
        return sharedPreferences!!.getBoolean( key.name, VALUE_BOOLEAN_FAIL)
    }

    fun removeKey(key: KEY_PREFERRENCE){
        sharedPreferences?.edit()?.remove(key.name)?.apply()
    }
}