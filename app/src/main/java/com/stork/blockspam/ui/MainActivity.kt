package com.stork.blockspam.ui

import android.os.Build
import android.os.Bundle
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseActivity
import com.stork.blockspam.storage.AppSharedPreferences
import com.stork.blockspam.ui.fragblockphone.BlockPhoneFragment
import com.stork.blockspam.ui.fragphone.PhoneFragment
import com.stork.blockspam.ui.fragrecentcall.RecentCallFragment
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

            AppSharedPreferences.getInstance(this)
                    .saveBoolean(AppSharedPreferences.KEY_PREFERRENCE.IS_FIRST_INSTALL, true)
        }

        setPermissionV2 { onStatusListener ->
            /*
            * neu ko la default phone thi xin quyen Block
            * */
//            if(!onStatusListener){
//                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                    checkPermissionForBlockSpamService()
//                }else{
//                    checkPermissionForBlockBroadcast()
//                }
//            }

            checkPermission()

            /*
            * catch number from Intent.ACTION_DIAL
            *
            * */
            val actionDialData = intent?.data
            if(actionDialData != null){
                if(actionDialData.toString().startsWith("tel:")){
                    val phoneNumb = actionDialData.toString().substring(4)

                    keyboardPhoneBottomSheet?.setNumberInit(phoneNumb)
                    keyboardPhoneBottomSheet?.show(supportFragmentManager, keyboardPhoneBottomSheet?.tag)
                }
            }

        }
    }

    private fun setView(){
        val fragments = listOf(
                BlockPhoneFragment(),
                ServerFragment(),
                UserFragment(),
                RecentCallFragment()
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