package com.stork.blockspam.ui

import android.os.Build
import android.os.Bundle
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseActivity
import com.stork.blockspam.storage.AppSharedPreferences
import com.stork.blockspam.ui.fragblockphone.BlockPhoneFragment
import com.stork.blockspam.ui.fragphone.PhoneFragment
import com.stork.blockspam.ui.fragserver.ServerFragment
import com.stork.blockspam.ui.fraguser.UserFragment
import com.stork.viewcustom.general.TabBarView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private lateinit var tabBarItems: List<TabBarView>
    private var keyboardPhoneBottomSheet: KeyboardPhoneBottomSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setView()
        setEvent()

        /* Only, the first open App will ask user allow to DIAL
        * */
        if(!AppSharedPreferences.getInstance(this).getBoolean(AppSharedPreferences.KEY_PREFERRENCE.IS_FIRST_INSTALL)){
            setPermissionV2 { onStatusListener ->  }

            AppSharedPreferences.getInstance(this)
                    .saveBoolean(AppSharedPreferences.KEY_PREFERRENCE.IS_FIRST_INSTALL, true)
        }

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            checkPermissionForBlockSpamService()
        }else{
            checkPermissionForBlockBroadcast()
        }

    }

    private fun setView(){
        val fragments = listOf(
                BlockPhoneFragment(),
                ServerFragment(),
                UserFragment(),
                PhoneFragment()
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

        selectTab(AppSharedPreferences.getInstance(this).getInt(AppSharedPreferences.KEY_PREFERRENCE.TAB_SELECTED)?:0)
        keyboardPhoneBottomSheet = KeyboardPhoneBottomSheet()
    }

    private fun setEvent(){
        fabShowKeyboardPhone.setOnClickListener {
            keyboardPhoneBottomSheet?.show(supportFragmentManager, keyboardPhoneBottomSheet?.tag)
        }
    }

    //////

    private fun selectTab(index: Int) {
        tabBarItems.forEachIndexed { i, tabBarItem ->
            tabBarItem.setActive(i == index)
        }

        if(AppSharedPreferences.getInstance(this).getInt(AppSharedPreferences.KEY_PREFERRENCE.TAB_SELECTED) == index){return}
        AppSharedPreferences.getInstance(this).saveInt(AppSharedPreferences.KEY_PREFERRENCE.TAB_SELECTED, index)

        mainViewPager.setCurrentItem(index, false)
    }

}