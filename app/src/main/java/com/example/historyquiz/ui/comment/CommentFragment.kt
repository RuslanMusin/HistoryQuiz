package com.example.historyquiz.ui.comment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.example.historyquiz.model.comment.Comment
import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const.TAG_LOG
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.layout_add_comment.*
import java.util.*

abstract class CommentFragment: BaseFragment(), CommentView {

    protected lateinit var adapter: CommentAdapter

    private var comments: MutableList<Comment> = ArrayList()

    abstract var commentPresenter: CommentPresenter

    protected abstract var type: String
    protected abstract var elemId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCommentRecycler()
        setCommentListeners()
//        commentPresenter.repository = repository
    }

    override fun setCommentListeners() {
        et_comment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Log.d(TAG_LOG, "char length = " + charSequence.length)
                sendButton.isEnabled = charSequence.toString().trim { it <= ' ' }.length > 0
                Log.d(TAG_LOG, "enabled = " + sendButton.isEnabled)
            }

            override fun afterTextChanged(editable: Editable) {
                val charSequence = editable.toString()
                Log.d(TAG_LOG, "after char length = " + charSequence.length)
                sendButton.isEnabled = charSequence.trim { it <= ' ' }.length > 0
                Log.d(TAG_LOG, "enabled = " + sendButton.isEnabled)
            }
        })

        sendButton.setOnClickListener {
            sendComment(elemId)
           /* if (hasInternetConnection()) {
                sendComment()
            } else {
                showSnackBar(R.string.internet_connection_failed)
            }*/
        }
    }

    override fun initCommentRecycler() {
        adapter = CommentAdapter(ArrayList(), this)
        val manager = LinearLayoutManager(this.activity)
        rv_list.setLayoutManager(manager)
        adapter.attachToRecyclerView(rv_list)
        adapter.setOnItemClickListener(this)
    }

    override fun onReplyClick(position: Int) {
        et_comment.isEnabled = true
        val comment = comments.get(position)
        val commentString = comment.authorName + ", "
        et_comment.setText(commentString)
        et_comment.isPressed = true
        et_comment.setSelection(commentString.length)
    }

    override fun onAuthorClick(userId: String) {
//        commentPresenter.openCommentAuthor(userId)
    }

    override fun goToCommentAuthor(user: User) {
//        PersonalActivity.start(activity as Activity, step)
    }

    override fun setComments(comments: List<Comment>) {
        this.comments = comments.toMutableList()
        adapter.changeDataSet(comments)
    }

    override fun addComment(comment: Comment) {
        comments.add(comment)
        adapter.changeDataSet(comments)
    }

    override fun onItemClick(item: Comment) {
    }

    override fun showComments(comments: List<Comment>) {
        this.comments = comments.toMutableList()
        Log.d(TAG_LOG, "comments size = ${comments.size}")
        adapter.changeDataSet(comments)
    }

    override fun sendComment(elemId: String) {
        Log.d(TAG_LOG, "focus down")
        val commentText = et_comment.getText().toString()
        Log.d(TAG_LOG, "send comment = $commentText")
        if (commentText.length > 0) {
            val comment = Comment()
            val user = AppHelper.currentUser
            user.let {
                comment.content = commentText
                comment.authorId = user.id
                comment.authorName = user.username
                comment.authorPhotoUrl = user.photoUrl
//                comment.authorName = "${user.lastname} ${user.name} ${user.patronymic}"
                comment.createdDate = (Date()).time
                commentPresenter.createComment(type, elemId, comment)
            }
            addComment(comment)
        }
        clearAfterSendComment()
    }

}