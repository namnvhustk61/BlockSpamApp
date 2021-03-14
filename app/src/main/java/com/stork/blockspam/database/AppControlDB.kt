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
    fun  getCallPhoneDAO(): CallPhoneDAO?{
        return database?.callPhoneDAO
    }

}