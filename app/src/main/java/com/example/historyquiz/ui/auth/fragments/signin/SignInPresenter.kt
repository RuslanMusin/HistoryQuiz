package com.example.historyquiz.ui.auth.fragments.signin

import android.text.TextUtils
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.R
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
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG_LOG, "signInWithEmail:success")
                    val user = fireAuth!!.currentUser

//                    saveInPreferences(email,password)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG_LOG, "signInWithEmail:failure", task.exception)
                    updateUI(null)
                }

                viewState.hideProgressDialog()
            })
    }

 /*   private fun saveInPreferences(email: String, password: String) {
        val mySharedPreferences = logView.getSharedPreferences(USER_DATA_PREFERENCES, Context.MODE_PRIVATE)
        if(!mySharedPreferences.contains(USER_USERNAME)) {
            val editor = mySharedPreferences.edit()
            editor.putString(USER_USERNAME, email)
            editor.putString(USER_PASSWORD, password)
            editor.apply()
        }
    }*/

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
                    Log.d(TAG_LOG, "have user")
                    AppHelper.currentUser = user
                    it.status = ONLINE_STATUS
//                    userEpochRepository.get().createStartEpoches(user)
                    userRepository.changeUserStatus(it).subscribe()
                    gameRepository.removeRedundantLobbies(true)
                    viewState.goToProfile(it) }
            }
        } else {
            viewState.showError()
        }
    }

}