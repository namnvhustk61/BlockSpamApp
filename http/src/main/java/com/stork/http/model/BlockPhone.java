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
}
