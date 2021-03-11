package com.stork.blockspam.database

import android.content.Context
import androidx.room.Room


class AppControlDB {

    companion object {
        private var instance: AppControlDB? = null
        private var database: AppDatabase? = null

        fun getInstance(context: Context) :AppControlDB{
            if(instance == null){
                instance = AppControlDB()
                database = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.KEY_DATABASE)
                        .allowMainThreadQueries()
                        .build()
            }
            return instance!!
        }
    }

    /*
    *  CallPhoneDAO
    */
    fun insertCallPhone(callPhone: CallPhone){
        val dao :CallPhoneDAO? = database?.callPhoneDAO
        dao?.insert(callPhone)
    }

    fun updateCallPhone(callPhone: CallPhone){
        val dao :CallPhoneDAO? = database?.callPhoneDAO
        dao?.update(callPhone)
    }

    fun getAllCallPhone(): List<CallPhone>?{
        val dao :CallPhoneDAO? = database?.callPhoneDAO
        return dao?.all
    }
    fun phoneHasDB(phone: String): Boolean{
        val dao :CallPhoneDAO? = database?.callPhoneDAO
        val values = dao?.getByPhone(phone)?.size
        if (values == null || values==0)(return false)
        return true

    }
}