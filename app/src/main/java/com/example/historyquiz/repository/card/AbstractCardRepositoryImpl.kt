package com.example.historyquiz.repository.card

import android.util.Log
import com.example.historyquiz.model.card.AbstractCard
import com.example.historyquiz.model.db_dop_models.ElementId
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.gson
import com.example.historyquiz.utils.RxUtils
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class AbstractCardRepositoryImpl @Inject constructor() : AbstractCardRepository {

    private val databaseReference: DatabaseReference

    val TABLE_NAME = "abstract_cards"
    val USERS_ABSTRACT_CARDS = "users_abstract_cards"

    private val FIELD_ID = "id"
    private val FIELD_WIKI_URL = "wikiUrl"
    private val FIELD_NAME = "name"
    private val FIELD_LOWER_NAME = "lowerName"
    private val FIELD_PHOTO_URL = "photoUrl"
    private val FIELD_EXTRACT = "extract"
    private val FIELD_DESCRIPTION = "description"


    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    override fun toMap(card: AbstractCard?): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        val id = databaseReference!!.push().key
        card?.let {
            card.id = id
            result[FIELD_ID] = card.id
            result[FIELD_NAME] = card.name
            result[FIELD_LOWER_NAME] = card.lowerName
            result[FIELD_PHOTO_URL] = card.photoUrl
            result[FIELD_WIKI_URL] = card.wikiUrl
            result[FIELD_EXTRACT] = card.extract
            result[FIELD_DESCRIPTION] = card.description
        }
        return result
    }

    override fun toMapId(value: String?): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        result[FIELD_ID] = value

        return result
    }

    fun getKey(crossingId: String): String? {
        return databaseReference!!.child(crossingId).push().key
    }

    override fun findAbstractCardId(wikiUrl: String?): Single<String> {
        val single: Single<String> = Single.create { e ->
            Log.d(TAG_LOG, "wikiUrl = $wikiUrl")
            val query: Query? = databaseReference.orderByChild(FIELD_WIKI_URL).equalTo(wikiUrl)

            query?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var cardId: String = "null"
                    if (dataSnapshot.exists()) {
                        Log.d(TAG_LOG, "data exists")
                        for (snap in dataSnapshot.children) {
                            val card: AbstractCard? = snap.getValue(AbstractCard::class.java)
                            card?.id?.let {
                                cardId = it
                                Log.d(TAG_LOG, "abs card exists")
                            }
                        }
                    }
                    e.onSuccess(cardId)
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findAbstractCard(cardId: String?): Single<AbstractCard> {
        var card: AbstractCard?
        val query: Query = databaseReference.child(cardId!!)
        val single: Single<AbstractCard> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    card = dataSnapshot.getValue(AbstractCard::class.java)
                    e.onSuccess(card!!)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findAbstractCards(cardsIds: List<String>): Single<List<AbstractCard>> {
        val single: Single<List<AbstractCard>> = Single.create { e ->
            Observable
                .fromIterable(cardsIds)
                .flatMap {
                    this.findAbstractCard(it).toObservable()
                }
                .toList()
                .subscribe { cards ->
                    e.onSuccess(cards)
                }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findMyAbstractCards(userId: String): Single<List<AbstractCard>> {
        val single: Single<List<AbstractCard>> = Single.create { e ->
            val query: Query = databaseReference.root.child(USERS_ABSTRACT_CARDS).child(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(ElementId::class.java)
                        elementId?.let { elementIds.add(it.id) }
                    }
                    findAbstractCards(elementIds).subscribe { cards ->
                        e.onSuccess(cards)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findMyAbstractCardsByQuery(queryPart: String, userId: String): Single<List<AbstractCard>> {
        if (queryPart.equals("")) {
            return findMyAbstractCards(userId)
        } else {
            val single: Single<List<AbstractCard>> = Single.create { e ->
                val query: Query = databaseReference.root.child(USERS_ABSTRACT_CARDS).child(userId)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val elementIds: MutableList<String> = ArrayList()
                        for (snapshot in dataSnapshot.children) {
                            val elementId = snapshot.getValue(ElementId::class.java)
                            elementId?.let {
                                Log.d(TAG_LOG, "elem json = ${gson.toJson(elementId)}")
                                Log.d(TAG_LOG, "absCardId = ${it.id}")
                                elementIds.add(it.id)
                            }
                        }
                        findAbstractCards(elementIds).subscribe { cards ->
                            val pattern: Pattern = Pattern.compile("${queryPart.toLowerCase()}.*")
                            val cardsQuery: List<AbstractCard> =
                                cards.filter { e -> pattern.matcher(e.name?.toLowerCase()).matches() }.toMutableList()
                            e.onSuccess(cardsQuery)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
            return single.compose(RxUtils.asyncSingle())
        }
    }


    fun findDefaultAbstractCardsByQuery(userQuery: String, userId: String): Single<List<AbstractCard>> {
        var query: Query = databaseReference.root.child(USERS_ABSTRACT_CARDS).child(userId)
        val single: Single<List<AbstractCard>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(ElementId::class.java)
                        elementId?.let { elementIds.add(it.id) }
                    }
                    val queryPart = userQuery.toLowerCase()
                    query = databaseReference.orderByChild(FIELD_LOWER_NAME).startAt(queryPart).endAt(queryPart + Const.QUERY_END)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val cards: MutableList<AbstractCard> = ArrayList()
                            for(snapshot in dataSnapshot.children) {
                                val card = snapshot.getValue(AbstractCard::class.java)
                                if(elementIds.contains(card?.id)) {
                                    card?.isOwner = true
                                }
                                card?.let { cards.add(it) }

                            }
                            cards.let { e.onSuccess(it) }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })

                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })


        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findDefaultAbstractCards(userId: String): Single<List<AbstractCard>> {
        var query: Query = databaseReference.root.child(USERS_ABSTRACT_CARDS).child(userId)
        val single: Single<List<AbstractCard>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(ElementId::class.java)
                        elementId?.let { elementIds.add(it.id) }
                    }
                    query = databaseReference
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val cards: MutableList<AbstractCard> = ArrayList()
                            for(snapshot in dataSnapshot.children) {
                                val card = snapshot.getValue(AbstractCard::class.java)
                                if(elementIds.contains(card?.id)) {
                                    card?.isOwner = true
                                }
                                card?.let { cards.add(it) }

                            }
                            cards.let { e.onSuccess(it) }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })

                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })


        }
        return single.compose(RxUtils.asyncSingle())
    }

}
