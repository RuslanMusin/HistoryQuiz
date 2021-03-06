package com.example.historyquiz.ui.comment
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.comment.Comment
import com.example.historyquiz.repository.card.CommentRepository
import com.example.historyquiz.ui.base.BasePresenter
import io.reactivex.Single
import javax.inject.Inject

@InjectViewState
open class CommentPresenter @Inject constructor() : BasePresenter<CommentView>() {

    @Inject
    lateinit var commentRepository: CommentRepository

    fun loadComments(type: String, elemId: String) {
//        viewState.startTimeout(R.string.failed_load_comments)
        val single: Single<List<Comment>> = commentRepository.getComments(type, elemId)
        val disposable = single
            .doOnSubscribe({ viewState.showListLoading() })
                .doAfterTerminate({ viewState.hideListLoading() })
                .subscribe { res ->
                    viewState.showComments(res)
                  /* interceptSecondResponse(res, {
                       viewState.stopTimeout()
                       viewState.showComments(it)
                   },
                       R.string.failed_load_comments)*/
                }
        disposable?.let { compositeDisposable.add(it) }
    }

    fun createComment(type: String, elemId: String, comment: Comment) {
//        viewState.startTimeout (R.string.failed_post_comment)
        val single: Single<Boolean> = commentRepository.createComment(type, elemId, comment)
        val disposable = single
            ?.subscribe { res ->
//                viewState.showSnackBar("Comment added")
//                interceptSecondResponse(res, {viewState.stopTimeout()}, R.string.failed_post_comment)
            }
        disposable?.let { compositeDisposable.add(it) }
    }

    fun openCommentAuthor(userId: String) {

    }

}