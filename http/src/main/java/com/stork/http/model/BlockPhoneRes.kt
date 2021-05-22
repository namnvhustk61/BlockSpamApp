package com.stork.http.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BlockPhoneRes(
        @SerializedName("note")
        @Expose
        var note: String,

        @SerializedName("count")
        @Expose
        var count: String
)