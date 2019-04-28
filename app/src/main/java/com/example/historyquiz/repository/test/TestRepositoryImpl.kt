package com.example.historyquiz.repository.test

import android.util.Log
import com.example.historyquiz.model.db_dop_models.ElementId
import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.model.test.result.TestResult
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.card.AbstractCardRepository
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.epoch.EpochRepository
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.AFTER_TEST
import com.example.historyquiz.utils.Const.BEFORE_TEST
import com.example.historyquiz.utils.Const.BOT_ID
import com.example.historyquiz.utils.Const.LOSE_GAME
import com.example.historyquiz.utils.Const.NEW_ONES
import com.example.historyquiz.utils.Const.OLD_ONES
import com.example.historyquiz.utils.Const.QUERY_END
import com.example.historyquiz.utils.Const.SEP
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_POINTS
import com.example.historyquiz.utils.Const.WIN_GAME
import com.example.historyquiz.utils.RxUtils
import com.google.firebase.database.*
import dagger.Lazy
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class TestRepositoryImpl @Inject constructor() : TestRepository{

    @Inject
    lateinit var abstractCardRepository: AbstractCardRepository
    @Inject
    lateinit var epochRepository: EpochRepository
    @Inject
    lateinit var cardRepository: Lazy<CardRepository>
    @Inject
    lateinit var userRepository: UserRepository

    private val databaseReference: DatabaseReference

    private val TABLE_NAME = "tests"
    private val USERS_TESTS = "users_tests"
    val USERS_ANSWERS = "users_answers"
    val USERS_CARDS = "users_cards"
    val USERS_ABSTRACT_CARDS = "users_abstract_cards"

    private val TEST_QUESTIONS = "test_questions"
    private val TEST_CARDS = "test_cards"
    private val ABSTRACT_CARDS = "abstract_cards"


    private val FIELD_ID = "id"
    private val FIELD_TITLE = "title"
    private val FIELD_LOWER_TITLE = "lowerTitle"
    private val FIELD_CARD_ID = "cardId"
    private val FIELD_AUTHOR_ID = "authorId"
    private val FIELD_AUTHOR_NAME = "authorName"
    private val FIELD_QUESTIONS = "questions"
    private val FIELD_LINKS = "links"
    private val FIELD_DESC = "desc"
    private val FIELD_TYPE = "type"
    private val FIELD_IMAGE_URL = "imageUrl"
    private val FIELD_EPOCH_ID = "epochId"
    private val FIELD_USERS = "usersIds"


    private val FIELD_RELATION = "relation"


    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    override fun toMap(test: Test): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        result[FIELD_ID] = test.id
        result[FIELD_DESC] = test.desc
        result[FIELD_TITLE] = test.title
        result[FIELD_LOWER_TITLE] = test.lowerTitle
        result[FIELD_AUTHOR_ID] = test.authorId
        result[FIELD_AUTHOR_NAME] = test.authorName
        result[FIELD_CARD_ID] = test.cardId
        result[FIELD_QUESTIONS] = test.questions
        result[FIELD_LINKS] = test.links
        result[FIELD_IMAGE_URL] = test.imageUrl
        result[FIELD_EPOCH_ID] = test.epochId

        return result
    }

    override fun toMap(id: String?): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result[FIELD_ID] = id
        return result
    }

    override fun changeStatus(testId: String, userId: String, relation: String): Single<Relation> {
        Log.d(TAG_LOG,"change test status")
        val query: Query = databaseReference.root.child(USERS_TESTS).child(userId).child(testId)
        val single: Single<Relation> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(shapshot: DataSnapshot) {
                    var testRelation: Relation? = shapshot.getValue(Relation::class.java)
                    if(testRelation != null) {
                        Log.d(TAG_LOG,"rel not null")
                        when  {
                            WIN_GAME.equals(testRelation.relation) -> {
                                testRelation.relBefore = WIN_GAME
                                if(relation.equals(WIN_GAME)) {
                                    testRelation.relation = WIN_GAME
                                }
                                if(relation.equals(LOSE_GAME)) {
                                    testRelation.relation = BEFORE_TEST
                                }
                                if(relation.equals(AFTER_TEST)) {
                                    testRelation.relation = AFTER_TEST
                                }
                            }
                            AFTER_TEST.equals(testRelation.relation) -> {
                                testRelation.relBefore = AFTER_TEST
                                if(relation.equals(WIN_GAME)) {
                                    testRelation.relation = AFTER_TEST
                                }
                                if(relation.equals(LOSE_GAME)) {
                                    testRelation.relation = LOSE_GAME
                                }
                                if(relation.equals(AFTER_TEST)) {
                                    testRelation.relation = AFTER_TEST
                                }
                            }
                            LOSE_GAME.equals(testRelation.relation) -> {
                                testRelation.relBefore = LOSE_GAME
                                if(relation.equals(WIN_GAME)) {
                                    testRelation.relation = WIN_GAME
                                }
                                if(relation.equals(LOSE_GAME)) {
                                    testRelation.relation = LOSE_GAME
                                }
                                if(relation.equals(AFTER_TEST)) {
                                    testRelation.relation = AFTER_TEST
                                }
                            }
                        }
                    } else {
                        Log.d(TAG_LOG,"rel == null")
                        testRelation = Relation()
                        testRelation.relBefore = BEFORE_TEST
                    }
                    e.onSuccess(testRelation)

                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun createTest(test: Test, user: User): Single<Boolean> {

        val single: Single<Boolean> = Single.create { e ->
            val card = test.card
            val abstractCard = card?.abstractCard
            /* if(type.equals("read")) {
             Log.d(TAG_LOG,"read")
             abstractCardRepository.findAbstractCard(test, user,this)
         } else {*/
            abstractCardRepository
                    .findAbstractCardId(abstractCard?.wikiUrl)
                    .subscribe { cardId ->
                        val childUpdates = HashMap<String, Any>()
                        Log.d(TAG_LOG, "create")
                        val crossingKey = databaseReference.push().key
                        test.id = crossingKey!!
                        Log.d(TAG_LOG, "abstract")
                        card?.testId = test.id
                        if (cardId.equals("null")) {
                            Log.d(TAG_LOG, "createAbs")
                            val abstractCardValues = abstractCardRepository.toMap(abstractCard)
                            childUpdates[ABSTRACT_CARDS + SEP + abstractCard?.id] = abstractCardValues

                        } else {
                            abstractCard?.id = cardId
                            Log.d(TAG_LOG, "no create abs")
                        }
                        card?.cardId = abstractCard?.id
                        card?.epochId = test.epochId
                        Log.d(TAG_LOG, "after abstract")
                        val crossingIdValues = cardRepository.get().toMap(card)
                        childUpdates[TEST_CARDS + SEP + card?.id] = crossingIdValues

                        val addAbstractCardValues = abstractCardRepository.toMapId(card?.cardId)
                        childUpdates[USERS_ABSTRACT_CARDS + Const.SEP + BOT_ID + SEP + card?.cardId] =
                                addAbstractCardValues

                        val addCardValues = cardRepository.get().toMapId(card?.id)
                        childUpdates[USERS_CARDS + Const.SEP + BOT_ID + SEP + card?.id] =
                                addCardValues


                        test.lowerTitle = test.title?.toLowerCase()
                        test.authorId = user.id
                        test.authorName = user.username
                        test.cardId = card?.id
                        test.imageUrl = abstractCard?.photoUrl

                        val crossingValues = toMap(test)

                        childUpdates["$TABLE_NAME/$crossingKey"] = crossingValues

                        databaseReference.root.updateChildren(childUpdates)

                        e.onSuccess(true)
                    }
        }

        return single.compose(RxUtils.asyncSingle())

    }

    override fun finishTest(test: Test, user: User): Single<Boolean> {
        val childUpdates = HashMap<String, Any?>()
        val card = test.card
        val single: Single<Boolean> = Single.create { e ->
            test.id?.let { testId ->
                user.id?.let { userId ->
                    card!!.cardId!!.let { cardId ->
                        cardRepository.get().findMyAbstractCardStates(cardId, userId)
                            ?.subscribe { winnerCards ->
                                Log.d(TAG_LOG, "add card after test")
                                cardRepository.get().isUserHasCard(user.id, card.id!!).subscribe { has ->
                                    if(test.procent >= 70) {
                                        if (winnerCards.size == 0) {
                                            Log.d(TAG_LOG, "add abstract card")
                                            val addAbstractCardValues = abstractCardRepository.toMapId(cardId)
                                            childUpdates[USERS_ABSTRACT_CARDS + Const.SEP + userId + SEP + cardId] =
                                                    addAbstractCardValues
                                        }
                                        if (!has) {
                                            val addCardValues = cardRepository.get().toMapId(card.id)
                                            childUpdates[USERS_CARDS + Const.SEP + userId + SEP + card.id] =
                                                    addCardValues
                                        }
                                    }
                                    val addTestValues = toMap(test.id)
                                    childUpdates[USERS_TESTS + Const.SEP + userId + SEP + test.id] = addTestValues
                                    user.points += TEST_POINTS
                                    Log.d(TAG_LOG, "user points = ${user.points}")
                                    if (user.points >= user.nextLevel) {
                                        user.nextLevel = (1.5 * user.points + 20 * user.level).toLong()
                                        user.level++
                                        user.points = 0
                                    }
                                    userRepository.updateUser(user)
                                    databaseReference.root.updateChildren(childUpdates)
                                    e.onSuccess(true)
                                }

                            }
                    }
                }

            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findUserAnswers(userId: String, testId: String): Single<TestResult> {
        val query: Query = databaseReference.root.child(USERS_ANSWERS).child(userId).child(testId)
        val single : Single<TestResult> =  Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val testResult = dataSnapshot.getValue(TestResult::class.java)
                    e.onSuccess(testResult!!)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun saveUserAnswers(userId: String, testResult: TestResult): Single<Boolean> {
        val single : Single<Boolean> =  Single.create { e ->
        databaseReference.root
            .child(USERS_ANSWERS)
            .child(userId)
            .child(testResult.id)
            .setValue(testResult).addOnCompleteListener { it -> e.onSuccess(true) }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun readTest(testId: String?): Single<Test> {
        var test: Test?
        val query: Query = databaseReference.child(testId!!)
        val single : Single<Test> =  Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    test = dataSnapshot.getValue(Test::class.java)
                   e.onSuccess(test!!)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findTests(cardsIds: List<String>): Single<List<Test>> {
        val single: Single<List<Test>> = Single.create{e ->
            Observable
                    .fromIterable(cardsIds)
                    .flatMap {
                        this.readTest(it).toObservable()
                    }
                    .toList()
                    .subscribe{cards ->
                        e.onSuccess(cards)
                    }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findTests(userId: String, type: String): Single<List<Test>> {
        var query: Query = databaseReference.root.child(USERS_TESTS).child(userId)
        val single: Single<List<Test>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val relations = HashMap<String, ElementId>()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(ElementId::class.java)
                        elementId?.let {
                            relations[it.id] = it
                        }
                    }
                    query = databaseReference
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val cards: MutableList<Test> = ArrayList()
                            for(snapshot in dataSnapshot.children) {
                                val test = snapshot.getValue(Test::class.java)
                                if (relations.keys.contains(test?.id)) {
                                    test?.testDone = true
                                }
                                epochRepository.findEpoch(test?.epochId!!).subscribe { it ->
                                    test.epoch = it
                                }
                                if(type.equals(NEW_ONES) && !test.testDone) {
                                    cards.add(test)
                                } else if(type.equals(OLD_ONES) && test?.testDone!!) {
                                    cards.add(test)
                                }


                            }
                            e.onSuccess(cards)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })

                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })


        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findTestsByQuery(userId: String, userQuery: String, type: String): Single<List<Test>> {
        var query: Query = databaseReference.root.child(USERS_TESTS).child(userId)
        val single: Single<List<Test>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(Relation::class.java)
                        elementId?.let {
                            if(LOSE_GAME.equals(it.relation) || AFTER_TEST.equals(it.relation)) {
                                elementIds.add(it.id)
                            }
                        }
                    }
                    val queryPart = userQuery.toLowerCase()
                    query = databaseReference.orderByChild(FIELD_LOWER_TITLE).startAt(queryPart).endAt(queryPart + QUERY_END)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val cards: MutableList<Test> = ArrayList()
                            for(snapshot in dataSnapshot.children) {
                                val test = snapshot.getValue(Test::class.java)
                                    if (elementIds.contains(test?.id)) {
                                        test?.testDone = true
                                    } else {
                                        test?.testDone = false
                                    }
                                epochRepository.findEpoch(test?.epochId!!).subscribe { it ->
                                    test.epoch = it
                                }
                                if(type.equals(NEW_ONES) && !test?.testDone!!) {
                                    test?.let { cards.add(it) }
                                } else if(type.equals(OLD_ONES) && test?.testDone!!) {
                                    test?.let { cards.add(it) }
                                }

                            }
                            e.onSuccess(cards)
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
