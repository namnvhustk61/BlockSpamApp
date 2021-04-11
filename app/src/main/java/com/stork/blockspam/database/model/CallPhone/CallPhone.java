package com.stork.blockspam.database.model.CallPhone;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.stork.blockspam.AppConfig;
import com.stork.blockspam.database.AppControlDB;

import java.util.ArrayList;
import java.util.List;

@Entity(
        tableName = "TbCallPhone",
        indices = {@Index(value = {"id"},
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
        CallPhoneDAO dao = AppControlDB.Companion.getInstance(context).getCallPhoneDAO();
        if(!hasDB(context, this.phone)){
            try{
                if (dao != null) {
                    dao.insert(this);
                    return AppConfig.SUCCESS;
                }
            }catch (Exception e) {
                return  AppConfig.EXCEPTION;
            }
        }
        return AppConfig.ERROR;
    }

    @Ignore
    public Boolean deleteDb(Context context){
        CallPhoneDAO dao = AppControlDB.Companion.getInstance(context).getCallPhoneDAO();
        if (dao != null) {
            dao.delete(this);
            return true;
        }
        return false;
    }

    @Ignore
    public void updateDB__changeStatus(Context context){

        if(this.status.equals(CallPhoneKEY.STATUS.getSTATUS_BLOCK())){
            this.status = CallPhoneKEY.STATUS.getSTATUS_UNBLOCK();
        }else {
            this.status = CallPhoneKEY.STATUS.getSTATUS_BLOCK();
        }
        CallPhoneDAO dao = AppControlDB.Companion.getInstance(context).getCallPhoneDAO();
        if (dao != null) {
            dao.update(this);
        }

    }




    /*******  Static   *******/
    @Ignore
    public static   List<CallPhone> getAllDB(Context context){
        CallPhoneDAO dao = AppControlDB.Companion.getInstance(context).getCallPhoneDAO();
        if (dao != null) {
            return dao.getAll();
        }else {return  new ArrayList<>();}
    }

    @Ignore
    public static  List<CallPhone> deleteAllDB(Context context){
        CallPhoneDAO dao = AppControlDB.Companion.getInstance(context).getCallPhoneDAO();
        if (dao != null) {
            dao.deleteAll();
            return dao.getAll();
        }else {return  new ArrayList<>();}
    }

    @Ignore
    public static List<CallPhone> updateAllDB__Status_Block(Context context){
        CallPhoneDAO dao = AppControlDB.Companion.getInstance(context).getCallPhoneDAO();
        if(dao != null){
             dao.updateStatusAll(CallPhoneKEY.STATUS.getSTATUS_BLOCK());
             return dao.getAll();
        }
        return  new ArrayList<>();
    }

    @Ignore
    public static List<CallPhone> updateAllDB__Status_UNBLOCK(Context context){
        CallPhoneDAO dao = AppControlDB.Companion.getInstance(context).getCallPhoneDAO();
        if(dao != null){
            dao.updateStatusAll(CallPhoneKEY.STATUS.getSTATUS_UNBLOCK());
            return dao.getAll();
        }
        return  new ArrayList<>();
    }

    @Ignore
    public static boolean hasDB(Context context, String phone ){
        CallPhoneDAO dao = AppControlDB.Companion.getInstance(context).getCallPhoneDAO();
        if (dao != null) {
            List<CallPhone> values = dao.getByPhone(phone);
            if(values == null || values.size() == 0){return false;}
            return true;
        }
        return false;
    }

    @Ignore
    public static boolean isBlockDB(Context context, String phone ){
        List<String> lsPhoneCheck = new ArrayList<>();
        lsPhoneCheck.add(phone);
        if(phone.startsWith("0")){
            String phoneSub0 = phone.substring(1);
            lsPhoneCheck.add(phoneSub0); // remove 0
            lsPhoneCheck.add("+84" + phoneSub0);
        }
        if(phone.startsWith("+")){
            String phoneSubPlusXX = phone.substring(3);
            lsPhoneCheck.add(phoneSubPlusXX); // remove 0
            lsPhoneCheck.add("0" + phoneSubPlusXX);
        }
        /*
         * this.phone  9899999999
         **/
        CallPhoneDAO dao = AppControlDB.Companion.getInstance(context).getCallPhoneDAO();
        if (dao != null) {
            for (String p : lsPhoneCheck){
                List<CallPhone> values = dao.getByPhone(p);
                if(values != null && values.size() > 0){
                    if(values.get(0).status.equals(CallPhoneKEY.STATUS.getSTATUS_BLOCK())){return true;}
                }
            }
        }
        return false;
    }

}
