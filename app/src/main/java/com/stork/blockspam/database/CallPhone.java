package com.stork.blockspam.database;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.stork.blockspam.AppConfig;

import java.util.List;

@Entity(
        tableName = "TbCallPhone",
        indices = {@Index(value = {"phone"},
                unique = true)}
)
public class CallPhone{
    @PrimaryKey
    public Long id;
    public String name;
    public String phone;
    public String type;
    public String status;

    // Action

    @Ignore
    public int insertDB(Context context){
        if(!AppControlDB.Companion.getInstance(context).phoneHasDB(this.phone)){
            try{
                AppControlDB.Companion.getInstance(context).insertCallPhone(this);
                return AppConfig.SUCCESS;
            }catch (Exception e) {
                return  AppConfig.EXCEPTION;
            }
        }
        return AppConfig.ERROR;
    }

    @Ignore
    public void updateDB__changeStatus(Context context){

        if(this.status.equals(CallPhoneKEY.STATUS.getSTATUS_BLOCK())){
            this.status = CallPhoneKEY.STATUS.getSTATUS_UNBLOCK();
        }else {
            this.status = CallPhoneKEY.STATUS.getSTATUS_BLOCK();
        }
        AppControlDB.Companion.getInstance(context).updateCallPhone(this);
    }


    @Ignore
    public static List<CallPhone> getAllDB(Context context){
        return AppControlDB.Companion.getInstance(context).getAllCallPhone();
    }

}
