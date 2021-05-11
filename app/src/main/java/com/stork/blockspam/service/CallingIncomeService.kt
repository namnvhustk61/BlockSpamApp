package com.stork.blockspam.service

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import com.stork.blockspam.ui.callingincome.CallManager
import com.stork.blockspam.ui.callingincome.CallingIncomeActivity

class CallingIncomeService : InCallService() {
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        val intent = Intent(this, CallingIncomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        CallManager.call = call
        CallManager.inCallService = this
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        CallManager.call = null
        CallManager.inCallService = null
    }
}
