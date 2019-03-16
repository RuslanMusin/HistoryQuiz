package com.example.historyquiz.ui.comment
import com.example.historyquiz.model.comment.Comment
import com.example.historyquiz.repository.card.CardCommentRepositoryImpl
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.Const.CARD_COMMENT_TYPE
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import javax.inject.Inject

open class CommentPresenter: BasePresenter<CommentView>() {

    @Inject
    lateinit var commentRepositoryImpl: CardCommentRepositoryImpl

    fun loadComments(map: HashMap<String, String>, type: String) {
//        viewState.startTimeout(R.string.failed_load_comments)
        var single: Single<Result<List<Comment>>>? = null
        when(type) {
            CARD_COMMENT_TYPE -> {
               /* single = commentRepositoryImpl
                    .findStepComments(map.getValue(CURATOR_KEY), map.getValue(WORK_KEY), map.getValue(STEP_KEY))*/
            }
        }
        val disposable = single?.let {
            it.doOnSubscribe({ viewState.showLoading(it) })
                .doAfterTerminate({ viewState.hideLoading() })
                .subscribe { res ->
                  /* interceptSecondResponse(res, {
                       viewState.stopTimeout()
                       viewState.showComments(it)
                   },
                       R.string.failed_load_comments)*/
                }
        }
        disposable?.let { compositeDisposable.add(it) }
    }

    fun createComment(type: String, comment: Comment) {
//        viewState.startTimeout (R.string.failed_post_comment)
        var single: Single<Result<Comment>>? = null
        when(type) {
            CARD_COMMENT_TYPE -> {
              /*  single = commentRepositoryImpl
                    .postStepComment(map.getValue(CURATOR_KEY), map.getValue(WORK_KEY), map.getValue(STEP_KEY), comment)*/
            }
        }
        val disposable = single
            ?.subscribe { res ->
//                interceptSecondResponse(res, {viewState.stopTimeout()}, R.string.failed_post_comment)
            }
        disposable?.let { compositeDisposable.add(it) }
    }

    fun openCommentAuthor(userId: String) {

    }

}