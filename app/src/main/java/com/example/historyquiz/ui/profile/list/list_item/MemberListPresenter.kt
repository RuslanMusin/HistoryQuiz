package com.example.historyquiz.ui.profile.list.list_item

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class MemberListPresenter @Inject constructor() : BasePresenter<MemberListView>() {
    @Inject
    lateinit var testRepository: TestRepository

    fun loadTests() {
        /*testRepository
            .findAll()
            .compose(PresentationSingleTransformer())
            .doOnSubscribe { viewState.showProgressDialog() }
            .doAfterTerminate { viewState.hideProgressDialog() }
            .subscribe({
                viewState.changeDataSet(it)
            }, {
                viewState.showSnackBar(exceptionProcessor.processException(it))
            }).disposeWhenDestroy()*/
//        viewState.showItems(list)
    }

}