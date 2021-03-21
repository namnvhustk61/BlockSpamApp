package com.stork.http.model;

import com.google.gson.annotations.SerializedName;

public class BlockPhone {
    @SerializedName("phone")
    public String phone;

    @SerializedName("name")
    public String name ;

    @SerializedName("type")
    public String type;
    @SerializedName("status")
    public String status;

    public BlockPhone(String phone, String name, String type, String status){
        this.phone = phone;
        this.name = name;
        this.type = type;
        this.status = status;

    }

}
