package com.example.historyquiz.ui.auth.fragments.signup

import com.arellomobile.mvp.MvpView
import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface SignUpView: BasicFunctional {

    fun showEmailError(hasError: Boolean)

    fun showPasswordError(hasError: Boolean)

    fun goToProfile(user: User)

}