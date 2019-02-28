package com.example.historyquiz.ui.auth.fragments.signup

import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.R
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.RepositoryProvider
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.Const.TAG_LOG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

@InjectViewState
class SignUpPresenter: BasePresenter<SignUpView>() {

    init {
        App.sAppComponent.inject(this)
    }


    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Inject
    lateinit var fireAuth: FirebaseAuth

    internal fun createAccount(user: User, email: String, password: String) {
        Log.d(TAG_LOG, "createAccount:$email")
        if (!validateForm(email, password)) {
            return
        }

        viewState.showProgressDialog(R.string.progress_message)

        fireAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener( {task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG_LOG, "createUserWithEmail:success")
                    createInDatabase(user)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG_LOG, "createUserWithEmail:failure", task.exception)
                    viewState.hideProgressDialog()
                }
                viewState.hideProgressDialog()
            })
    }

    private fun createInDatabase(user: User) {
        fireAuth.currentUser?.uid?.let { user.id = it }
        RepositoryProvider.userRepository.createUser(user)
    }

    private fun validateForm(email: String, password: String): Boolean {
        return checkEmail(email) && checkPassword(password)
    }

    private fun checkEmail(email: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            viewState.showEmailError(true)
            false
        } else {
            viewState.showEmailError(false)
            true
        }
    }

    private fun checkPassword(password: String): Boolean {
        return if (TextUtils.isEmpty(password)) {
            viewState.showPasswordError(true)
            false
        } else {
            viewState.showPasswordError(false)
            true
        }
    }


    private fun updateUI(user: User) {
        viewState.hideProgressDialog()
        viewState.goToProfile(user)
    }
}