package com.example.historyquiz.ui.auth.fragments.signin

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.auth.fragments.signup.SignUpFragment
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.navigation.NavigationView
import com.example.historyquiz.utils.AppHelper.Companion.currentUser
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_DATA_PREFERENCES
import com.example.historyquiz.utils.Const.USER_PASSWORD
import com.example.historyquiz.utils.Const.USER_USERNAME
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_sign_in.*
import javax.inject.Inject
import javax.inject.Provider

class SignInFragment : BaseFragment(), SignInView, View.OnClickListener {

    @Inject
    lateinit var gson: Gson

    @InjectPresenter
    lateinit var presenter: SignInPresenter
    @Inject
    lateinit var presenterProvider: Provider<SignInPresenter>
    @ProvidePresenter
    fun providePresenter(): SignInPresenter = presenterProvider.get()

    override fun showBottomNavigation(navigationView: NavigationView) {
        Log.d(TAG_LOG, "fragment hide bottom navigation")
        navigationView.hideBottomNavigation()
        navigationView.setBottomNavigationStatus(false)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        setListeners()
    }

    private fun signUp(v: View) {
        val args = Bundle()
        args.putString(KEY, "Button")
        removeStackDownTo()
        val fragment = SignUpFragment.newInstance(args)
        pushFragments(fragment, true)
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

    private fun setListeners() {
        btn_sign_up.setOnClickListener(this)
        btn_enter.setOnClickListener(this)
        iv_cover.setOnClickListener(this)
        tv_name.setOnClickListener(this)
        iv_epoch.setOnClickListener { presenter.createEpoches(resources.getStringArray(R.array.epoches).toList()) }
    }

    override fun onClick(view: View) {

        when (view.id) {

            R.id.btn_enter -> {
                val username = et_email.getText().toString();
                val password = et_password.getText().toString();
                presenter.signIn(username, password)
            }

            R.id.tv_name -> {
                et_email.setText("rust@ma.ru")
                et_password.setText("rustamka")

            }

            R.id.iv_cover -> {
                et_email.setText("ryst@ma.ru")
                et_password.setText("rystamka")
            }

            R.id.btn_sign_up -> signUp(view)
        }
    }

    override fun goToProfile(user: User) {
        Log.d(TAG_LOG,"login and ${currentUser.email} and ${user.email}")
        removeStackDownTo()
        val args = Bundle()
        args.putString(Const.USER_ITEM, gson.toJson(currentUser))
        openNavigationPage()
       /* Navigation.findNavController(btn_enter)
            .navigate(R.id.action_loginFragment_to_profileFragment, args)*/
    }

    override fun showError() {
        ti_email.error = getString(R.string.enter_correct_name)
        ti_password.error = getString(R.string.enter_correct_password)
    }

    override fun createCookie(email: String, password: String) {
        activity?.getSharedPreferences(USER_DATA_PREFERENCES, Context.MODE_PRIVATE)?.let {
            if (!it.contains(USER_USERNAME)) {
                val editor = it.edit()
                editor.putString(USER_USERNAME, email)
                editor.putString(USER_PASSWORD, password)
                editor.apply()
            }
        }
    }

    companion object {

        const val KEY = "TITLE"

        fun newInstance(args: Bundle): Fragment {
            val fragment = SignInFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            return SignInFragment()
        }
    }
}