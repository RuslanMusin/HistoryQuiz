package com.example.historyquiz.ui.profile.list.list_item

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const.DEFAULT_USERS_TYPE
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class MemberListPresenter @Inject constructor() : BasePresenter<MemberListView>() {

    @Inject
    lateinit var userRepository: UserRepository
    
    fun loadReadersByQuery(query: String) {
        userRepository
            .loadUsersByQuery(query)
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it.toMutableList()) }, { viewState.handleError(it) })
    }

    fun loadUsersByQueryAndType(query: String, userId: String, type: String) {
        userRepository!!
            .findUsersByTypeByQuery(query, userId, type)
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it.toMutableList()) }, { viewState.handleError(it) })
    }

    /* @SuppressLint("CheckResult")
         fun loadRequestByQuery(query: String, userId: String) {
             userRepository!!
                     .loadRequestByQuery(query, userId)
                     .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                     .doAfterTerminate(Action { viewState.hideLoading() })
                     .subscribe({ this.setRequestsByQuery(it) }, { viewState.handleError(it) })
         }
     */
    @SuppressLint("CheckResult")
    fun loadUsers(type: String) {
        val single: Single<List<User>>
        if(type.equals(DEFAULT_USERS_TYPE)) {
            single = userRepository.loadDefaultUsers()
        } else {
            single = userRepository.findUsersByIdAndType(currentId, type)

        }
        single
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadNextElements(page: Int) {
        userRepository!!
            .loadDefaultUsers()
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .doAfterTerminate(Action { viewState.setNotLoading() })
            .subscribe({ viewState.changeDataSet(it.toMutableList()) }, { viewState.handleError(it) })

    }
}