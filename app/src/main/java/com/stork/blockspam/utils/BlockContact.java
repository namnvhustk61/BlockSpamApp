package com.stork.blockspam.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.BlockedNumberContract;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class BlockContact {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> get(Context context){

        try{
            List<String> lsPhone= new ArrayList<>();
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                    new String[]{BlockedNumberContract.BlockedNumbers.COLUMN_ID, BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                            BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER}, null, null, null);
            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String phoneNo = cur.getString(cur.getColumnIndex(
                            BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER));

                    lsPhone.add(phoneNo);
                }
            }

            if(cur!=null){
                cur.close();
            }

            return lsPhone;
        }catch (Exception e){
            return  null;
        }

    }
}
