package com.stork.blockspam.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseActivity
import com.stork.blockspam.ui.fragblockphone.BlockPhoneFragment
import com.stork.blockspam.ui.fragserver.ServerFragment
import com.stork.blockspam.ui.fraguser.UserFragment
import com.stork.blockspam.utils.AppPermission
import com.stork.blockspam.utils.AppSettingsManager
import com.stork.viewcustom.general.TabBarView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private var currentIndex = 0
    private lateinit var tabBarItems: List<TabBarView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setPermission()

        setView()
    }

    private fun setPermission(){
        /*
        *  Required  App is  caller id & spam app default  OR  Call Phone Default
        *   at some android SDK slowly  don't  have caller id & spam app default
        *   then require  Call Phone Default
        * */

        /* set APP is caller id & spam app default
         *
         *  status == true  -> device have caller id & spam app default
         *  status == false -> device don't have caller id & spam app default
         */
        val status :Boolean = AppSettingsManager.setDefaultAppBlockCall(this)
        if(!status){
            AppPermission.requirePermissions(
                this,
                arrayOf(AppPermission.PER_READ_CONTACTS, AppPermission.PER_READ_PHONE_STATE),
                AppPermission.PER_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AppPermission.onRequestPermissionsResult(requestCode, permissions, grantResults) {state: Boolean ->

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsManager.ROLE_CALL_SCREENING_ID) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                // Your app is now the call screening app
                AppPermission.requirePermissions(
                    this,
                    arrayOf(AppPermission.PER_READ_CONTACTS, AppPermission.PER_READ_PHONE_STATE),
                    AppPermission.PER_REQUEST_CODE
                )
            } else {
                // Your app is not the call screening app
                AppPermission.requirePermissions(
                    this,
                    arrayOf(
                        AppPermission.PER_READ_PHONE_STATE,
                        AppPermission.PER_READ_CALL_LOG,
                        Manifest.permission.ANSWER_PHONE_CALLS
                    ),
                    AppPermission.PER_REQUEST_CODE
                )
            }
        }
    }

    private fun setView(){
        val fragments = listOf(
                BlockPhoneFragment(),
                ServerFragment(),
                UserFragment(),
                Fragment()
        )

        mainViewPager.adapter = MainPagerAdapter(supportFragmentManager, fragments)
        mainViewPager.offscreenPageLimit = 4

        tabBarItems = listOf(
                tab0,
                tab1,
                tab2,
                tab3
        )

        tabBarItems.forEachIndexed { index, tabBarItem ->
            tabBarItem.setOnClickListener {
                selectTab(index)
            }
        }

        selectTab(0)
    }

    private fun selectTab(index: Int) {
        this.currentIndex = index

        tabBarItems.forEachIndexed { i, tabBarItem ->
            tabBarItem.setActive(i == index)
        }
        mainViewPager.setCurrentItem(index, false)
    }
}