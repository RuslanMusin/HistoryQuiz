package com.example.historyquiz.repository.card

import android.util.Log
import com.example.historyquiz.model.comment.Comment
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.RxUtils
import com.google.firebase.database.*
import io.reactivex.Single
import java.util.*
import javax.inject.Inject


class CommentRepositoryImpl @Inject constructor() : CommentRepository {

    val databaseReference: DatabaseReference

    @Inject
    lateinit var userRepository: UserRepository

    private val TABLE_NAME = "card_comments"

    val map: Map<String, String> = HashMap()

    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun readComment(pointId: String): DatabaseReference {
        return databaseReference.child(pointId)
    }

    fun deleteComment(pointId: String) {
        databaseReference.child(pointId).removeValue()
    }

    fun updateComment(comment: Comment) {
        val updatedValues = HashMap<String, Any>()
//        databaseReference.child(comment.id).updateChildren(updatedValues)
    }

    override fun getComments(type: String, testId: String): Single<List<Comment>> {
        val single: Single<List<Comment>> = Single.create { e ->
            val query: Query = getRef(type).child(testId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments: MutableList<Comment> = ArrayList()
                    for (postSnapshot in dataSnapshot.children) {
                        val comment = postSnapshot.getValue(Comment::class.java)!!
                        userRepository.readUserById(comment.authorId).subscribe { it ->
                            comment.authorName = it.username
                            comment.authorPhotoUrl = it.photoUrl
                            comments.add(comment)
                        }
                    }
                    e.onSuccess(comments)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(Const.TAG_LOG, "loadPost:onCancelled", databaseError.toException() as Throwable?)
                }
            })
        }
        return single.compose(RxUtils.asyncSingle())

    }

    override fun createComment(type: String, elemId: String, comment: Comment): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            val key = getRef(type).child(elemId).push().key
            comment.id = key
            getRef(type).child(elemId).child(key!!).setValue(comment)
            e.onSuccess(true)
        }
        return single.compose(RxUtils.asyncSingle())
    }

    private fun getRef(type: String): DatabaseReference {
        return databaseReference.child(type)
    }



}