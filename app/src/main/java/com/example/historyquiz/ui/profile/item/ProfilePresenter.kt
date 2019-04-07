package com.example.historyquiz.ui.profile.item

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.repository.api.WikiApiRepository
import com.example.historyquiz.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor() : BasePresenter<ProfileView>() {

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