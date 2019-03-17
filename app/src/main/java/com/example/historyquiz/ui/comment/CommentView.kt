package com.example.historyquiz.ui.comment
import com.example.historyquiz.model.comment.Comment
import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.BaseAdapter
import com.example.historyquiz.ui.base.interfaces.BasicFunctional
import io.reactivex.disposables.Disposable

interface CommentView: BasicFunctional, BaseAdapter.OnItemClickListener<Comment>{

    fun showComments(comments: List<Comment>)

    fun showLoading(disposable: Disposable)

    fun onReplyClick(position: Int)

    fun onAuthorClick(userId: String)

    fun setComments(comments: List<Comment>)

    fun addComment(comment: Comment)

    fun sendComment()

    fun clearAfterSendComment()

    fun goToCommentAuthor(user: User)

    fun setCommentListeners()

    fun initCommentRecycler()
}