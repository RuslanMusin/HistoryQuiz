package com.example.historyquiz.ui.game.add_photo

import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.db_dop_models.PhotoItem
import com.example.historyquiz.repository.card.AbstractCardRepository
import com.example.historyquiz.ui.base.BasePresenter
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

@InjectViewState
class AddPhotoPresenter @Inject constructor() : BasePresenter<AddPhotoView>() {

    @Inject
    lateinit var abstractCardRepository: AbstractCardRepository

    fun loadPhotos(userId: String) {
        abstractCardRepository
            .findMyAbstractCards(userId)
            .doOnSubscribe(Consumer<Disposable> { viewState.showLoading() })
            .doAfterTerminate(Action { viewState.hideLoading() })
            .map { it ->
                val list: MutableList<PhotoItem> =  ArrayList()
                for(item in it) {
                    if(item.photoUrl == null) {
                        continue
                    }
                    item.photoUrl?.let{list.add(PhotoItem(it))}
                }
                list
            }
            .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }
}