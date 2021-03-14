package com.stork.blockspam.database

enum class CallPhoneKEY{
    STATUS{
        override var STATUS_BLOCK = "0"
        override var STATUS_UNBLOCK = "1"
    },
    TYPE{
        override var TYPE_NORMAL = "0"
    };


    open lateinit var STATUS_BLOCK: String
    open lateinit var STATUS_UNBLOCK: String

    open lateinit var TYPE_NORMAL: String

}