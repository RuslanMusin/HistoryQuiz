package com.example.historyquiz.repository.card

import android.util.Log
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.db_dop_models.ElementId
import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.AFTER_TEST
import com.example.historyquiz.utils.Const.BOT_ID
import com.example.historyquiz.utils.Const.CARD_NUMBER
import com.example.historyquiz.utils.Const.DEFAULT_EPOCH_ID
import com.example.historyquiz.utils.Const.SEP
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.WIN_GAME
import com.example.historyquiz.utils.RxUtils
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor() : CardRepository {

    @Inject
    lateinit var testRepository: TestRepository

    @Inject
    lateinit var abstractCardRepository: AbstractCardRepository

    var databaseReference: DatabaseReference

    val TABLE_NAME = "test_cards"
    val USERS_CARDS = "users_cards"
    val USERS_TESTS = "users_tests"
    val USERS_ABSTRACT_CARDS = "users_abstract_cards"


    private val FIELD_ID = "id"
    private val FIELD_CARD_ID = "cardId"
    private val FIELD_TEST_ID = "testId"
    private val FIELD_EPOCH_ID = "epochId"
    private val FIELD_INTELLIGENCE = "intelligence"
    private val FIELD_SUPPORT = "support"
    private val FIELD_PRESTIGE = "prestige"
    private val FIELD_HP = "hp"
    private val FIELD_STRENGTH = "strength"
    private val FIELD_TYPE = "type"


    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    override fun toMap(card: Card?): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        val id = databaseReference!!.push().key
        card?.let {
            card.id = id
            result[FIELD_ID] = card.id
            result[FIELD_TEST_ID] = card.testId
            result[FIELD_CARD_ID] = card.cardId
            result[FIELD_EPOCH_ID] = card.epochId
            result[FIELD_INTELLIGENCE] = card.intelligence
            result[FIELD_PRESTIGE] = card.prestige
            result[FIELD_HP] = card.hp
            result[FIELD_SUPPORT] = card.support
            result[FIELD_STRENGTH] = card.strength
            result[FIELD_TYPE] = card.type
        }
        return result
    }

    override fun toMapId( value: String?): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result[FIELD_ID] = value
        return result
    }


    fun setDatabaseReference(path: String) {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun getKey(crossingId: String): String? {
        return databaseReference!!.child(crossingId).push().key
    }

    override fun addCardAfterGame(cardId: String , winnerId: String, loserId: String): Single<Boolean> {
        val childUpdates = HashMap<String, Any>()
        val single: Single<Boolean> = Single.create { e ->
            findMyCards(loserId).subscribe { loserCards ->
                readCard(cardId).subscribe { card ->
                            val addCardValues = toMapId(cardId)
                            childUpdates[USERS_CARDS + Const.SEP + winnerId + SEP + cardId] = addCardValues
                            Log.d(TAG_LOG, "cards winner path = ${USERS_CARDS + Const.SEP + winnerId + SEP + cardId}")
                            if(loserCards.size > CARD_NUMBER && !loserId.equals(BOT_ID)) {
                                val removeCardValues = toMapId(null)
                                childUpdates[USERS_CARDS + Const.SEP + loserId + SEP + cardId] = removeCardValues
                                Log.d(TAG_LOG, "cards loser path = ${USERS_CARDS + Const.SEP + loserId + SEP + cardId}")
                            }
                            card.cardId?.let { absCardId ->
                            findMyAbstractCardStates(absCardId, winnerId).subscribe { winnerCards ->
                                    if (winnerCards.size == 0) {
                                        val addAbstractCardValues = abstractCardRepository.toMapId(absCardId)
                                        childUpdates[USERS_ABSTRACT_CARDS + Const.SEP + winnerId + SEP + absCardId] =
                                                addAbstractCardValues
                                    }
                                    if (loserCards.size > CARD_NUMBER && !loserId.equals(BOT_ID)) {
                                        findMyAbstractCardStates(absCardId, loserId)
                                            .subscribe { loserCards ->
                                                if (loserCards.size == 1) {
                                                    val removeAbstractCardValues =
                                                        abstractCardRepository.toMapId(null)
                                                    childUpdates[USERS_ABSTRACT_CARDS + Const.SEP + loserId + SEP + absCardId] =
                                                            removeAbstractCardValues
                                                }
                                                databaseReference.root.updateChildren(childUpdates)
                                                e.onSuccess(true)
                                            }
                                    }
                                }

                        }
                    }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun readCard(cardId: String): Single<Card> {
        var card: Card?
        val query: Query = databaseReference.child(cardId)
        val single : Single<Card> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    card = dataSnapshot.getValue(Card::class.java)
                    abstractCardRepository
                            .findAbstractCard(card?.cardId)
                            .subscribe { t ->
                                card?.abstractCard = t
                                testRepository
                                        .readTest(card?.testId)
                                        .subscribe{ test ->
                                            card?.test = test
                                            e.onSuccess(card!!)
                                        }
                            }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun readCardForTest(cardId: String): Single<Card> {
        var card: Card?
        val query: Query = databaseReference.child(cardId)
        val single : Single<Card> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    card = dataSnapshot.getValue(Card::class.java)
                    AbstractCardRepositoryImpl()
                            .findAbstractCard(card?.cardId)
                            .subscribe { t ->
                                card?.abstractCard = t
                                e.onSuccess(card!!)
                            }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findCards(cardsIds: List<String>): Single<List<Card>> {
        val single: Single<List<Card>> = Single.create{e ->
            Observable
                    .fromIterable(cardsIds)
                    .flatMap {
                        this.readCard(it).toObservable()
                    }
                    .toList()
                    .subscribe{cards ->
                        e.onSuccess(cards)
                    }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findOfficialMyCards(userId: String): Single<List<Card>> {
        val single:Single<List<Card>> =  Single.create { e ->
            findMyCards(userId).subscribe { cards ->
                val officials: MutableList<Card> = ArrayList()
                for (card in cards) {
                    officials.add(card)
                }
                e.onSuccess(officials)
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findMyCards(userId: String): Single<List<Card>> {
        return Single.create { e ->
            val query: Query = databaseReference.root.child(USERS_CARDS).child(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(ElementId::class.java)
                        elementId?.let { elementIds.add(it.id) }
                    }
                    findCards(elementIds).subscribe{ cards ->
                        e.onSuccess(cards)
                    }

                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }
    }

    override fun isUserHasCard(userId: String, cardId: String): Single<Boolean> {
        val query: Query = databaseReference.root.child(USERS_CARDS).child(userId).child(cardId)
        val single: Single<Boolean> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()) {
                        e.onSuccess(true)
                    } else {
                        e.onSuccess(false)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findMyCardsByEpoch(userId: String, epochId: String): Single<List<Card>> {
        val single:Single<List<Card>> =  Single.create { e ->
            findMyCards(userId).subscribe { cards ->
                if(epochId.equals(DEFAULT_EPOCH_ID)) {
                    e.onSuccess(cards)
                } else {
                    val officials: MutableList<Card> = ArrayList()
                    for (card in cards) {
                        if (epochId.equals(card.epochId)) {
                            officials.add(card)
                        }
                    }
                    e.onSuccess(officials)
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findDefaultAbstractCardStates(abstractCardId: String): Single<List<Card>> {
        val query: Query = databaseReference.orderByChild(FIELD_CARD_ID).equalTo(abstractCardId)
        val single: Single<List<Card>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cards: MutableList<Card> = ArrayList()
                    for(snapshot in dataSnapshot.children) {
                        val card = snapshot.getValue(Card::class.java)
                        card?.let { cards.add(it) }

                    }
                    cards.let { e.onSuccess(it) }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }



    override fun findMyAbstractCardStates(abstractCardId: String, userId: String): Single<List<Card>> {
        val single: Single<List<Card>> = Single.create { e ->
            var query: Query = databaseReference.root.child(USERS_CARDS).child(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds:MutableList<String> = ArrayList()
                    for(snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(ElementId::class.java)
                        elementId?.let { elementIds.add(it.id) }
                    }
                    query = databaseReference.orderByChild(FIELD_CARD_ID).equalTo(abstractCardId)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val cards: MutableList<Card> = ArrayList()
                            for(snapshot in dataSnapshot.children) {
                                val card = snapshot.getValue(Card::class.java)
                                if(elementIds.contains(card?.id)) {
                                    card?.let { cards.add(it) }
                                }
                            }
                            e.onSuccess(cards)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }

                override fun onCancelled(p0: DatabaseError) {
                }

            })


        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findDefaultAbstractCardTests(abstractCardId: String): Single<List<Test>> {
        var query: Query = databaseReference.orderByChild(FIELD_CARD_ID).equalTo(abstractCardId)
        val single: Single<List<Test>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cards: MutableList<String> = ArrayList()
                    for(snapshot in dataSnapshot.children) {
                        val card = snapshot.getValue(Card::class.java)
                        card?.let { it.testId?.let { it1 -> cards.add(it1) } }
                    }
                    val list: Single<List<Test>> = Observable.fromIterable(cards).flatMap {
                        testRepository?.readTest(it)?.toObservable()
                    }.toList()
                    list.subscribe{tests ->
                        e.onSuccess(tests)
                    }
                }


                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findMyAbstractCardTests(abstractCardId: String, userId: String): Single<List<Test>> {
        val single: Single<List<Test>> = Single.create { e ->
            var query: Query = databaseReference.root.child(USERS_TESTS).child(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(Relation::class.java)
                        elementId?.let {
                            if (AFTER_TEST.equals(elementId.relation) || WIN_GAME.equals(elementId.relation)) {
                                elementIds.add(it.id)
                            }
                        }
                    }
                    query = databaseReference.orderByChild(FIELD_CARD_ID).equalTo(abstractCardId)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val cards: MutableList<String> = ArrayList()
                                for(snapshot in dataSnapshot.children) {
                                    val card = snapshot.getValue(Card::class.java)
                                    card?.let {
                                        it.testId?.let { it1 ->
                                            if (elementIds.contains(it1)) {
                                                cards.add(it1)
                                            }
                                        }
                                    }
                                }
                                val list: Single<List<Test>> = Observable.fromIterable(cards).flatMap {
                                    testRepository?.readTest(it)?.toObservable()
                                }.toList()
                                list.subscribe{tests ->
                                    e.onSuccess(tests)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })

                    }
                override fun onCancelled(p0: DatabaseError) {
                }

            })


        }
        return single.compose(RxUtils.asyncSingle())
    }
}
