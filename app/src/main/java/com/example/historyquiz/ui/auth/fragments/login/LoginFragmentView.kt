package com.example.historyquiz.ui.auth.fragments.login

import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface LoginFragmentView: BasicFunctional {

    fun showEmailError(hasError: Boolean)

    fun showPasswordError(hasError: Boolean)

    fun showError()

    fun createCookie(email: String, password: String)

    fun goToProfile(curator: User)
}