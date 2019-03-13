package com.example.historyquiz.ui.base.interfaces

import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.MvpView

interface BasicFunctional: MvpView {

    fun showProgressDialog(message: String)

    fun showProgressDialog(messageId: Int)

    fun hideProgressDialog()

    fun showSnackBar(message: String)

    fun showSnackBar(messageId: Int)

    fun hideBottomNavigation()

    fun showBottomNavigation()

    fun setActionBar(toolbar: Toolbar)

    fun setToolbarTitle(id: Int)

    fun changeWindowsSoftInputMode(mode: Int)

    fun pushFragments(fragment: Fragment, shouldAdd: Boolean)

    fun showFragment(lastFragment: Fragment, fragment: Fragment)

}