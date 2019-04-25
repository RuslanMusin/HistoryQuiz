package com.example.historyquiz.ui.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.historyquiz.R
import com.example.historyquiz.model.game.GameData
import com.example.historyquiz.repository.game.GameRepositoryImpl.Companion.FIELD_CREATOR
import com.example.historyquiz.repository.game.GameRepositoryImpl.Companion.FIELD_INVITED
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.interfaces.BasicFunctional
import com.example.historyquiz.ui.navigation.NavigationView
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const.IN_GAME_STATUS
import com.example.historyquiz.utils.Const.TAG_LOG
import dagger.android.support.AndroidSupportInjection

abstract class BaseFragment : MvpAppCompatFragment(), BasicFunctional {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (activity as BasicFunctional).setOfflineChecking()

    }

    override fun setOfflineChecking() {

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

    override fun setActionBarTitle(id: Int) {
        (activity as BasicFunctional).setActionBarTitle(id)
    }

    override fun setToolbarTitle(tvToolbar: TextView, title: String) {
        (activity as BasicFunctional).setToolbarTitle(tvToolbar, title)
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

    override fun hideFragment() {
        (activity as NavigationView).hideFragment()
    }

    override fun openLoginPage() {
        (activity as NavigationView).openLoginPage()
    }

    override fun openNavigationPage() {
        (activity as NavigationView).openNavigationPage()
    }

    protected open fun backFragment() {
        activity?.onBackPressed()
    }

    override fun showLoading() {
        (activity as BasicFunctional).showLoading()
    }

    override fun hideLoading() {
        (activity as BasicFunctional).hideLoading()
    }

    override fun performBackPressed() {
        (activity as BasicFunctional).performBackPressed()
    }

    override fun removeStackDownTo() {
        (activity as BasicFunctional).removeStackDownTo()
    }

    override fun removeStackDownTo(number: Int) {
        (activity as BasicFunctional).removeStackDownTo(number)
    }

    override fun setStatus(status: String) {
        (activity as BasicFunctional).setStatus(status)

    }

    override fun waitEnemy() {
        (activity as BasicFunctional).waitEnemy()
    }

}