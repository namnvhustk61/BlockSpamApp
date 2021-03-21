package com.stork.blockspam.database.model.DbBlockPhone;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.stork.blockspam.database.AppControlDB;

import java.util.ArrayList;
import java.util.List;

@Entity(
        tableName = "TbDbBlockPhone",
        indices = {@Index(value = {"phone"},
                unique = true)}
)
public class DbBlockPhone {
    @PrimaryKey
    public int id;
    public String phone;
    public String name ;
    public String type;
    public String status;
    public DbBlockPhone(int id, String phone, String name, String type, String status){
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.type = type;
        this.status = status;

    }

    @Ignore
    public void insertDB(Context context){
        DbBlockPhoneDAO dao = AppControlDB.Companion.getInstance(context).getDbBlockPhoneDAO();
        if (dao != null) {
            dao.insert(this);
        }
    }

    /*******  Static   *******/

    @Ignore
    public static List<DbBlockPhone> getAllDB(Context context){
        DbBlockPhoneDAO dao = AppControlDB.Companion.getInstance(context).getDbBlockPhoneDAO();
        if (dao != null) {
            return dao.getAll();
        }else {return  new ArrayList<>();}
    }

    @Ignore
    public static void insertAllDB(Context context, List<DbBlockPhone> list){
//        try{
            DbBlockPhoneDAO dao = AppControlDB.Companion.getInstance(context).getDbBlockPhoneDAO();
            if (dao != null) {
                dao.deleteAll();
                DbBlockPhone[] arr = new DbBlockPhone[list.size()];
                list.toArray(arr); // fill the array
                dao.insert(arr);
            }
//        }catch (Exception ignored){
//
//        }

    }
}
