package com.stork.http;

import com.google.gson.annotations.SerializedName;

public class ServiceResult<T> {

    @SerializedName("code")
    public String code;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public T data;

}