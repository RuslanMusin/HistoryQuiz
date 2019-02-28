package com.example.historyquiz.ui.auth.fragments.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.auth.fragments.login.LoginFragment.Companion.KEY
import com.example.historyquiz.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment: BaseFragment(), SignUpView, View.OnClickListener {

    @InjectPresenter
    lateinit var signUpPresenter: SignUpPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        setListeners()
    }

    private fun setListeners() {
        btn_sign_up.setOnClickListener(this)
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_up -> {
                val email = "rust@ma.ru"
                val password = "rustamka"
                val user = User()
                user.email = email
                user.photoUrl = "null"
                user.desc = "desc"
                user.lowerUsername = email.toLowerCase()
                signUpPresenter.createAccount(user, email, password)
            }

            R.id.btn_login -> goToLogin()
        }
    }

    override fun showEmailError(hasError: Boolean) {
        if(hasError) {
            ti_email.error = getString(R.string.enter_correct_name)
        } else {
            ti_email.error = null
        }

    }

    override fun showPasswordError(hasError: Boolean) {
        if(hasError) {
            ti_password.error = getString(R.string.enter_correct_password)
        } else {
            ti_password.error = null
        }

    }

    private fun goToLogin() {
        Navigation.findNavController(btn_login).navigate(R.id.loginFragment)
    }

    override fun goToProfile(user: User) {
        val args = Bundle()
        args.putString(KEY, "Button")
        Navigation.findNavController(btn_sign_up).navigate(R.id.navigationActivity, args)
    }


    companion object {

        private val GALLERY_PHOTO = 0

        private val STANDART_PHOTO = 1
    }
}