package com.example.historyquiz.ui.auth.fragments.signin

import android.text.TextUtils
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.R
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.repository.epoch.EpochRepository
import com.example.historyquiz.repository.epoch.UserEpochRepository
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const.ONLINE_STATUS
import com.example.historyquiz.utils.Const.TAG_LOG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.Lazy
import javax.inject.Inject

@InjectViewState
class SignInPresenter @Inject constructor() : BasePresenter<SignInView>() {

    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var gameRepository: GameRepository
    @Inject
    lateinit var userEpochRepository: Lazy<UserEpochRepository>
    @Inject
    lateinit var epochRepository: EpochRepository
    @Inject
    lateinit var fireAuth: FirebaseAuth


    fun signIn(email: String, password: String) {
        Log.d(TAG_LOG, "signIn:$email")
        if (!validateForm(email, password)) {
            return
        }

        viewState.showProgressDialog(R.string.progress_message)

        fireAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    val user = fireAuth!!.currentUser
                    viewState.createCookie(email,password)
                    updateUI(user)
                } else {
                    updateUI(null)
                }

                viewState.hideProgressDialog()
            })
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

    fun updateUI(firebaseUser: FirebaseUser?) {
        viewState.hideProgressDialog()
        if (firebaseUser != null) {
            userRepository?.readUserById(currentId)
                .subscribe { user ->
                user?.let {
                    Log.d(TAG_LOG, "have user and userEmail = ${it.email}")
                    AppHelper.currentUser = it
                    it.status = ONLINE_STATUS
                    userRepository.changeUserStatus(it).subscribe()
                    gameRepository.removeRedundantLobbies(true)
                    viewState.goToProfile(it)
                }
            }
        } else {
            viewState.showError()
        }
    }

    fun createEpoches(list: List<String>) {
        for (item in list) {
            epochRepository.createEpoch(Epoch(item.toString())).subscribe()
        }
    }

}