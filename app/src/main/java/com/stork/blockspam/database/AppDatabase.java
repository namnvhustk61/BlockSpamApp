package com.stork.blockspam.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CallPhone.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static String KEY_DATABASE = "BlockSpam";
    public abstract CallPhoneDAO getCallPhoneDAO();

}
