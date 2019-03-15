package com.example.historyquiz.ui.base

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable

open class BasePresenter<View: MvpView>: MvpPresenter<View>() {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

}