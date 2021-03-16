package com.stork.http.utils;

import org.json.JSONObject;

public class MyJSONObject {

    public static String getString(JSONObject js, String key){
        try{
            return js.getString(key);
        }catch (Exception e){
            return null;
        }
    }
}
