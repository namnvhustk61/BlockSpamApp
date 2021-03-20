package com.stork.http.model;

import com.google.gson.annotations.SerializedName;

public class AddPhoneCloud {
    @SerializedName("phone")
    public String phone;

    @SerializedName("name")
    public String name ;

    @SerializedName("type")
    public String type;
    @SerializedName("user_id")
    public String user_id;
}
