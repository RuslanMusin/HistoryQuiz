package com.example.historyquiz.ui.navigation

import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.MvpView
import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface NavigationView: BasicFunctional {



    fun setRequest(request: () -> Unit)

    fun showConnectionError()

    fun supportActionBar(toolbar: Toolbar)

    fun onBackPressed()

}