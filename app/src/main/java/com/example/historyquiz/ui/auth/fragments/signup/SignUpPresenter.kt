package com.example.historyquiz.ui.auth.fragments.signup

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.R
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.RepositoryProvider
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.AVATAR
import com.example.historyquiz.utils.Const.TAG_LOG
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@InjectViewState
class SignUpPresenter: BasePresenter<SignUpView>() {

    init {
        App.sAppComponent.inject(this)
    }

    @Inject
    lateinit var fireAuth: FirebaseAuth

    internal fun createAccount(user: User, imageUri: Uri?) {
        Log.d(TAG_LOG, "createAccount:")
        if (!validateForm(user)) {
            return
        }
        viewState.showProgressDialog(R.string.progress_message)
        fireAuth.createUserWithEmailAndPassword(user.email!!, user.password!!)
            .addOnCompleteListener( {task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG_LOG, "createUserWithEmail:success")
                    createInDatabase(user, imageUri)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG_LOG, "createUserWithEmail:failure", task.exception)
                    viewState.hideProgressDialog()
                }
                viewState.hideProgressDialog()
            })
    }

    private fun createInDatabase(user: User, imageUri: Uri?) {
        fireAuth.currentUser?.uid?.let { user.id = it }
        if(imageUri != null) {
            user.photoUrl = (Const.IMAGE_START_PATH + user.id + Const.SEP
                    + AVATAR)
            uploadPhoto(user, imageUri)
        }

        RepositoryProvider.userRepository.createUser(user)
    }

    private fun uploadPhoto(user: User, imageUri: Uri) {

            val childRef = AppHelper.storageReference.child(user.photoUrl!!)

            //uploading the image
            val uploadTask = childRef.putFile(imageUri)

            uploadTask.addOnSuccessListener { }.addOnFailureListener { }

    }

    private fun validateForm(user: User): Boolean {
        return checkEmail(user.email!!) && checkPassword(user.password!!)
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
        AppHelper.currentUser = user
        viewState.goToProfile(user)
    }
}