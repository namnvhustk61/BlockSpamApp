package com.stork.blockspam.database

import android.content.Context
import androidx.room.Room
import com.stork.blockspam.database.model.CallPhone.CallPhoneDAO
import com.stork.blockspam.database.model.DbBlockPhone.DbBlockPhoneDAO


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
    /*
   *  CallPhoneDAO
   */
    fun  getDbBlockPhoneDAO(): DbBlockPhoneDAO?{
        return database?.dbBlockPhoneDAO
    }

}