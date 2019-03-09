package com.example.historyquiz.ui.profile.item

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.R
import com.example.historyquiz.repository.api.WikiApiRepository
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.ui.navigation.NavigationView
import com.example.historyquiz.utils.Const.ACTION_QUERY
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TITLES
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class ProfilePresenter: BasePresenter<ProfileView>() {

    init {
        App.sAppComponent.inject(this)
    }

    @Inject
    lateinit var wikiApiRepository: WikiApiRepository

  /*  fun query(query: String) {
        Log.d(TAG_LOG,"pres opensearch")
        wikiApiRepository
            .query(TITLES)
            .doOnSubscribe(Consumer<Disposable> { viewState.showProgressDialog(R.string.progress_message) })
            .doAfterTerminate(Action { viewState.hideProgressDialog() })
            .subscribe({ viewState.setQueryResults(it) }, { viewState.handleError(it) })
    }*/

}