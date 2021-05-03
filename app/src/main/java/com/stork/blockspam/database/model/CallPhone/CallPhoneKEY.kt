package com.stork.blockspam.database.model.CallPhone

enum class CallPhoneKEY{
    STATUS{
        override var STATUS_BLOCK = "0"
        override var STATUS_UNBLOCK = "1"
    },
    TYPE{
        override var TYPE_NORMAL = "Other"
        override var TYPE_LOCAL = "Local"
        override var TYPE_ONLINE = "Online"
    };


    open lateinit var STATUS_BLOCK: String
    open lateinit var STATUS_UNBLOCK: String

    open lateinit var TYPE_NORMAL: String
    open lateinit var TYPE_LOCAL: String
    open lateinit var TYPE_ONLINE: String

}