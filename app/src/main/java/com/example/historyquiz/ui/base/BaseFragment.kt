package com.example.historyquiz.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.historyquiz.ui.base.interfaces.BasicFunctional
import com.example.historyquiz.ui.navigation.NavigationView
import dagger.android.support.AndroidSupportInjection

abstract class BaseFragment : MvpAppCompatFragment(), BasicFunctional {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun showProgressDialog(message: String) {
        (activity as BasicFunctional).showProgressDialog(message)
    }

    override fun showProgressDialog(messageId: Int) {
        (activity as BasicFunctional).showProgressDialog(messageId)
    }

    override fun hideProgressDialog() {
        (activity as BasicFunctional).hideProgressDialog()
    }

    override fun showSnackBar(message: String) {
        (activity as BasicFunctional).showSnackBar(message)
    }

    override fun showSnackBar(messageId: Int) {
        (activity as BasicFunctional).showSnackBar(messageId)
    }

    override fun setActionBar(toolbar: Toolbar) {
        (activity as BasicFunctional).setActionBar(toolbar)
    }

    override fun setToolbarTitle(id: Int) {
        (activity as BasicFunctional).setToolbarTitle(id)
    }

    override fun hideBottomNavigation() {
        (activity as BasicFunctional).hideBottomNavigation()
    }

    override fun showBottomNavigation(navigationView: NavigationView) {
        navigationView.showBottomNavigation(navigationView)
//        (activity as BasicFunctional).showBottomNavigation()
    }

    override fun changeWindowsSoftInputMode(mode: Int) {
        (activity as BasicFunctional).changeWindowsSoftInputMode(mode)
    }

    override fun pushFragments(fragment: Fragment, shouldAdd: Boolean) {
        (activity as NavigationView).pushFragments(fragment, shouldAdd)
    }

    override fun showFragment(lastFragment: Fragment, fragment: Fragment) {
        (activity as NavigationView).showFragment(lastFragment, fragment)
    }

    override fun openLoginPage() {
        (activity as NavigationView).openLoginPage()
    }

    override fun openNavigationPage() {
        (activity as NavigationView).openNavigationPage()
    }
}