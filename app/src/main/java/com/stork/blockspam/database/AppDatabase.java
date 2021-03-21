package com.stork.blockspam.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.stork.blockspam.database.model.CallPhone.CallPhone;
import com.stork.blockspam.database.model.CallPhone.CallPhoneDAO;
import com.stork.blockspam.database.model.DbBlockPhone.DbBlockPhone;
import com.stork.blockspam.database.model.DbBlockPhone.DbBlockPhoneDAO;

@Database(entities = {CallPhone.class, DbBlockPhone.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static String KEY_DATABASE = "BlockSpam";

    public abstract CallPhoneDAO getCallPhoneDAO();

    public abstract DbBlockPhoneDAO getDbBlockPhoneDAO();

}
