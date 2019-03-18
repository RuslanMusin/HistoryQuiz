package com.example.historyquiz.ui.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.historyquiz.R
import com.example.historyquiz.ui.auth.fragments.login.LoginFragment
import com.example.historyquiz.ui.base.BaseActivity
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.base.interfaces.BasicFunctional
import com.example.historyquiz.ui.profile.item.ProfileFragment
import com.example.historyquiz.ui.tests.test_list.TestListFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_ITEM
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.layout_connectivity.*
import java.util.*
import javax.inject.Inject

open class NavigationActivity: BaseActivity(), NavigationView, View.OnClickListener {

    @InjectPresenter
    lateinit var navigationPresenter: NavigationPresenter

    @Inject
    lateinit var gson: Gson

    private lateinit var stacks: HashMap<String, Stack<Fragment>>
    private lateinit var relativeTabs: HashMap<String, String>

    var currentTab: String = TAB_AUTH
    private var showTab: String = SHOW_AUTH

    var lastRequest: (() -> Unit)? = null

    companion object {

        const val TAG_NAVIG_ACT = "TAG_NAVIG_ACTIVITY"

        const val TAB_AUTH = "tab_auth"
        const val TAB_PROFILE = "tab_profile"
        const val TAB_GAME = "tab_game"
        const val TAB_CARDS = "tab_cards"
        const val TAB_TESTS = "tab_tests"
        const val SHOW_AUTH = "show_auth"
        const val SHOW_PROFILE = "show_profile"
        const val SHOW_GAME = "show_game"
        const val SHOW_TESTS = "show_tests"
        const val SHOW_CARDS = "show_works"


        fun start(context: Context) {
            val intent = Intent(context, NavigationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        initBottomNavigation()
        initListeners()

        stacks = HashMap()
        stacks[TAB_PROFILE] = Stack()
        stacks[TAB_CARDS] = Stack()
        stacks[TAB_GAME] = Stack()
        stacks[TAB_TESTS] = Stack()
        stacks[TAB_AUTH] = Stack()

        stacks[SHOW_PROFILE] = Stack()
        stacks[SHOW_GAME] = Stack()
        stacks[SHOW_TESTS] = Stack()
        stacks[SHOW_CARDS] = Stack()

        relativeTabs = HashMap()
        relativeTabs[TAB_AUTH] = SHOW_AUTH
        relativeTabs[TAB_PROFILE] = SHOW_PROFILE
        relativeTabs[TAB_GAME] = SHOW_GAME
        relativeTabs[TAB_TESTS] = SHOW_TESTS
        relativeTabs[TAB_CARDS] = SHOW_CARDS

        openLoginPage()
//        bottom_navigation.selectedItemId = R.id.action_profile
    }



    override fun openLoginPage() {
        currentTab = TAB_AUTH
        showTab = SHOW_AUTH
        val fragment = LoginFragment.newInstance()
        pushFragments(fragment, true)


    }

    override fun openNavigationPage() {
        currentTab = TAB_PROFILE
        showTab = SHOW_PROFILE
        bottom_navigation.selectedItemId = R.id.action_profile

    }

    private fun initListeners() {
        iv_reconnect.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id) {

            R.id.iv_reconnect -> {
                lastRequest?.let {
                    showLoading()
                    it()
                }
            }
        }
    }

    override fun showConnectionError() {
        Log.d(TAG_LOG, "connection error")
        li_offline.visibility = View.VISIBLE
        layout_connectivity.visibility = View.VISIBLE
        container.visibility = View.GONE
        layout_loading.visibility = View.GONE
    }

    override fun showLoading() {
        li_offline.visibility = View.VISIBLE
        layout_loading.visibility = View.VISIBLE
        layout_connectivity.visibility = View.GONE
        container.visibility = View.GONE
    }

    override fun hideLoading() {
        li_offline.visibility = View.GONE
        layout_loading.visibility = View.GONE
        container.visibility = View.VISIBLE
    }

    override fun setRequest(request: () -> Unit) {
        lastRequest = request
    }

    override fun supportActionBar(toolbar: Toolbar) {
        if(supportActionBar == null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    private fun initBottomNavigation() {
        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        bottomNavigationView.isItemHorizontalTranslationEnabled = false
        bottomNavigationView.setOnNavigationItemSelectedListener(
            object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    when (item.getItemId()) {
                        R.id.action_profile -> {
                            selectedTab(TAB_PROFILE)
                        }

                        R.id.action_tests -> {
                            selectedTab(TAB_TESTS)
                        }

                        R.id.action_cards -> {
                            selectedTab(TAB_CARDS)
                        }

                        R.id.action_game -> {
                            selectedTab(TAB_GAME)
                        }
                    }
                    return true
                }
            })
    }

    private fun selectedTab(tabId: String) {
        showLoading()
        val lastTab = currentTab
        currentTab = tabId
        relativeTabs[currentTab]?.let { showTab = it }
        val size = stacks[tabId]?.size
        setSupportActionBar(null)
        if (size == 0) {
            when(tabId) {

                TAB_PROFILE -> showProfile(tabId)

                TAB_GAME -> showGame(tabId)

                TAB_CARDS -> showCards(tabId)

                TAB_TESTS -> showTests(tabId)
            }
        } else {
            if(stacks[showTab]?.size == 0) {
                stacks[currentTab]?.lastElement()?.let {
                    pushFragments(it, false)
                }
            } else {
                val ft = supportFragmentManager.beginTransaction()
                val iterator = stacks[showTab]?.iterator()
                iterator?.let {
                    stacks[showTab]?.firstElement()?.let { it1 -> ft.replace(R.id.container, it1) }
                    for((i, frag) in iterator.withIndex()) {
                        if(i > 0) {
                            ft.hide(frag.targetFragment!!).add(R.id.container, frag).show(frag)
                        }
                    }
                }
//                stacks[showTab]?.lastElement()?.let {
//                (it as BaseFragment<*>).showBottomNavigation()
//                ft.hide(it.targetFragment!!).add(R.id.container, it).show(it)
                ft.commit()

                //                    ft.replace(R.id.container, it)
//                    ft.hide(it.targetFragment!!).add(R.id.container, it).show(it)
                /*  stacks[lastTab]?.lastElement()?.let { it1 ->
                    ft.hide(it1).add(R.id.container, it).show(it)*/
            }


        }
    }

    override fun pushFragments(fragment: Fragment, shouldAdd: Boolean) {
        showBottomNavigation(this)
        if (shouldAdd) {
            stacks[currentTab]?.push(fragment)
        }
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        (fragment as BaseFragment).showBottomNavigation(this)
        ft.replace(R.id.container, fragment)
        ft.commit()
    }

    override fun onBackPressed() {
        val fragment: Fragment? = getCurrentFragment()
        (fragment as BasicFunctional).performBackPressed()
    }

    override fun performBackPressed() {
        showBottomNavigation(this)
        if(stacks[showTab]?.size!! > 1) {
            hideFragment()
        } else {
            popCurrentFragment()
        }
    }

    override fun removeStackDownTo() {
        stacks[showTab]?.clear()
    }

    override fun removeStackDownTo(number: Int) {
        for(i in 1..number) {
            stacks[showTab]?.pop()
        }
    }

    private fun getCurrentFragment(): Fragment? {
        val fragment = stacks[currentTab]?.size?.minus(1)?.let { stacks[currentTab]?.elementAt(it) }
        return fragment

    }

    private fun popCurrentFragment() {
        val size = stacks.get(currentTab)?.size
        if(size == 1){
            finish();
            return;
        }
        popFragments();
    }

    fun popFragments() {
        val fragment = stacks[currentTab]?.size?.minus(2)?.let { stacks[currentTab]?.elementAt(it) }

        stacks[currentTab]?.pop()

        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        fragment?.let {
            (it as BaseFragment).showBottomNavigation(this@NavigationActivity)
            ft.replace(R.id.container, it) }
        ft.commit()
    }

    override fun hideFragment() {
        val num = stacks[showTab]?.size?.minus(2)
        Log.d(TAG_LOG, "num = ${num} and size = ${stacks[showTab]?.size}")
        var fragment = num?.let { stacks[showTab]?.elementAt(it) }

        val fragmentBefore = stacks[showTab]?.pop()
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        fragmentBefore?.let { ft.hide(it).remove(fragmentBefore) }
        val size = stacks.get(showTab)?.size
        if(size!! == 1){
            stacks[showTab]?.pop()
        }
        fragment?.let {
            (it as BaseFragment).showBottomNavigation(this@NavigationActivity)
            Log.d(TAG_LOG, "show")
            ft.show(it)
        }

        ft.commit()
    }

    override fun showFragment(lastFragment: Fragment, fragment: Fragment) {
        if(stacks[showTab]?.size == 0) {
            stacks[showTab]?.push(lastFragment)
        }
        stacks[showTab]?.push(fragment)
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        lastFragment.let {
            (it as BaseFragment).showBottomNavigation(this@NavigationActivity)
            ft.hide(it).add(R.id.container, fragment).show(fragment)
        }
        ft.commit()
    }

    private fun showProfile(tabId: String) {
        Log.d(TAG_NAVIG_ACT, "curator start")
        val args: Bundle = Bundle()
        args.putString(USER_ITEM, gson.toJson(AppHelper.currentUser))
        val fragment = ProfileFragment.newInstance(args)
        pushFragments(fragment, true)
        /*args.putString(ID_KEY, AppHelper.currentCurator.id)
        val fragment = CuratorFragment.newInstance(args, this)
        pushFragments(fragment, true)*/
    }

    private fun showGame(tabId: String) {
        showProfile(tabId)
       /* val fragment = StudentListFragment.newInstance(this)
        pushFragments(fragment, true)*/
    }

    private fun showCards(tabId: String) {
        showProfile(tabId)
     /*   val fragment = ThemeListFragment.newInstance(this)
        pushFragments(fragment, true)*/
    }

    private fun showTests(tabId: String) {
        val fragment = TestListFragment.newInstance()
        pushFragments(fragment, true)
    }

}