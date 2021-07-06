package com.stork.blockspam.storage

import android.content.Context

class Constant {
    companion object{
        private var instance: Constant? = null
        fun getInstance(): Constant {
            if (instance == null) {
                instance = Constant()
            }
            return instance as Constant
        }
    }

}


private const val PATH = "com.stork.blockspam.action."
const val ACCEPT_CALL = PATH + "accept_call"
const val DECLINE_CALL = PATH + "decline_call"

const val CALL_BACK = PATH + "call_back"

const val ACTION_BLOCK = PATH + "action_block"
const val ACTION_SEND_MESSAGE = PATH + "action_send_message"

const val ID_NOTIFICATION = 10