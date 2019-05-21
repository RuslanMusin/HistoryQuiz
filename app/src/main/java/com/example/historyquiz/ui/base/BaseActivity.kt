package com.example.historyquiz.ui.base

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatActivity
import com.example.historyquiz.ui.base.interfaces.BasicFunctional
import com.example.historyquiz.ui.navigation.NavigationView
import com.example.historyquiz.utils.Const.TAG_LOG
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_navigation.*
import javax.inject.Inject

abstract class BaseActivity : MvpAppCompatActivity(), HasSupportFragmentInjector, BasicFunctional {

    var progressDialog: ProgressDialog? = null

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentInjector
    }

    override fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog?.let{
            it.setMessage(message)
            it.isIndeterminate = true
            it.setCancelable(false)
        }
        progressDialog?.show()
    }

    override fun showProgressDialog(messageId: Int) {
        showProgressDialog(getString(messageId))
    }

    override fun hideProgressDialog() {
        progressDialog?.let {
            if (it.isShowing) {
                progressDialog!!.dismiss()
            }
        }
    }

    override fun showSnackBar(message: String) {
        val snackbar: Snackbar = Snackbar.make(findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG)
        snackbar.getView().setBackgroundColor(Color.BLACK)
        val textView = snackbar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView;
        textView.setTextColor(Color.WHITE);
        snackbar.show()
    }

    override fun showSnackBar(messageId: Int) {
        showSnackBar(getString(messageId))
    }

    override fun setActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    override fun setActionBarTitle(id: Int) {
        supportActionBar?.title = getString(id)
    }

    override fun setToolbarTitle(tvToolbar: TextView, title: String) {
        tvToolbar.text = title
    }

    override fun hideBottomNavigation() {
        Log.d(TAG_LOG, "hide bottom navigation")
        bottom_navigation.visibility = View.GONE
        changeWindowsSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }


    override fun showBottomNavigation(navigationView: NavigationView) {
        Log.d(TAG_LOG, "show bottom navigation")
        bottom_navigation.visibility = View.VISIBLE
        changeWindowsSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun changeWindowsSoftInputMode(mode: Int) {
        this.window.setSoftInputMode(mode);
    }

}