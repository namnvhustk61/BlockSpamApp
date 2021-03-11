package com.stork.blockspam.database;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "TbCallPhone")
public class CallPhone{
    @PrimaryKey
    public Long id;
    public String name;
    public String phone;
    public String type;
    public String status;

    // Action
    public void insertDB(Context context){
        AppControlDB.Companion.getInstance(context).insertCallPhone(this);
    }

    public void updateDB__changeStatus(Context context){

        if(this.status.equals(CallPhoneKEY.STATUS.getSTATUS_BLOCK())){
            this.status = CallPhoneKEY.STATUS.getSTATUS_UNBLOCK();
        }else {
            this.status = CallPhoneKEY.STATUS.getSTATUS_BLOCK();
        }
        AppControlDB.Companion.getInstance(context).updateCallPhone(this);
    }

    public static List<CallPhone> getAllDB(Context context){
        return AppControlDB.Companion.getInstance(context).getAllCallPhone();
    }

}
