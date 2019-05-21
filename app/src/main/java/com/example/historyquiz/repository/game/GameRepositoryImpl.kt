package com.example.historyquiz.repository.game

import android.util.Log
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.game.CardChoose
import com.example.historyquiz.model.game.GameData
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.game.LobbyPlayerData
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.epoch.UserEpochRepository
import com.example.historyquiz.repository.user.UserRepositoryImpl
import com.example.historyquiz.repository.user.UserRepositoryImpl.Companion.FIELD_LOBBY_ID
import com.example.historyquiz.repository.user.UserRepositoryImpl.Companion.FIELD_RELATION
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.AppHelper.Companion.currentUser
import com.example.historyquiz.utils.AppHelper.Companion.userInSession
import com.example.historyquiz.utils.Const.BOT_ID
import com.example.historyquiz.utils.Const.IN_GAME_STATUS
import com.example.historyquiz.utils.Const.MODE_END_GAME
import com.example.historyquiz.utils.Const.NOT_ACCEPTED
import com.example.historyquiz.utils.Const.OFFICIAL_TYPE
import com.example.historyquiz.utils.Const.OFFLINE_STATUS
import com.example.historyquiz.utils.Const.ONLINE_GAME
import com.example.historyquiz.utils.Const.ONLINE_STATUS
import com.example.historyquiz.utils.Const.QUERY_END
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.RxUtils
import com.example.historyquiz.utils.getRandom
import com.google.firebase.database.*
import io.reactivex.Single
import javax.inject.Inject
import kotlin.random.Random

class GameRepositoryImpl @Inject constructor() : GameRepository {

    @Inject
    lateinit var cardRepository: CardRepository

    @Inject
    lateinit var userEpochRepository: UserEpochRepository

    @Inject
    lateinit var userRepository: UserRepositoryImpl

    val allDbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    val TABLE_GAMES = "games_v3"
    val gamesDbRef: DatabaseReference = allDbRef.child(TABLE_GAMES)

    val TABLE_SEARCHING = "searching"
    val searchingDbRef: DatabaseReference = gamesDbRef.child(TABLE_SEARCHING)

    val lobbiesDbRef: DatabaseReference = gamesDbRef.child(TABLE_LOBBIES)

    val CREATOR_LOBBY = "creator"
    val LOBBY_TYPE = "type"
    val FIELD_LOWER_TITLE = "lobby_title"
    lateinit var nowSearchingDbRef: DatabaseReference


    lateinit var currentLobbyRef: DatabaseReference
    lateinit var creatorLobbyRef: DatabaseReference
    lateinit var invitedLobbyRef: DatabaseReference

    lateinit var myLobbyRef: DatabaseReference
    lateinit var enemyLobbyRef: DatabaseReference

    var callbacks: InGameCallbacks? = null

    var lastEnemyChoose: CardChoose? = null
    var lastMyChosenCardId: String? = null

    var enemyId: String? = null

    var enemy_answers = 0;
    var enemy_score = 0;

    var my_answers = 0;
    var my_score = 0;

    var onYouLoseCard: Card? = null
    var onEnemyLoseCard: Card? = null

    var listeners = HashMap<DatabaseReference, ValueEventListener>()

    private val databaseReference: DatabaseReference


    val TABLE_NAME = "lobbies"

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

    override fun setLobbyRefs(lobbyId: String) {
        currentLobbyRef = databaseReference.child(lobbyId)

        currentUser?.let {
            if(it.gameLobby?.gameData?.role.equals(FIELD_INVITED)) {
                myLobbyRef = currentLobbyRef.child(FIELD_INVITED)
                enemyLobbyRef = currentLobbyRef.child(FIELD_CREATOR)
            } else {
                myLobbyRef = currentLobbyRef.child(FIELD_CREATOR)
                enemyLobbyRef = currentLobbyRef.child(FIELD_INVITED)
            }
        }
        /*  creatorLobbyRef = currentLobbyRef.child(FIELD_CREATOR)
          invitedLobbyRef = currentLobbyRef.child(FIELD_INVITED)*/
    }

    override fun removeLobby(id: String) {
        Log.d(TAG_LOG,"remove lobby $id")
        databaseReference.child(id).removeValue()
        currentUser?.let {
            it.lobbyId = null
            databaseReference.root.child(UserRepositoryImpl.TABLE_NAME).child(it.id).child(FIELD_LOBBY_ID).setValue(null)
            databaseReference.root.child(USERS_LOBBIES).child(it.id).child(id).setValue(null)
        }
    }


    fun resetData() {

        callbacks = null
        lastEnemyChoose = null
        lastMyChosenCardId = null
        enemyId = null
        enemy_answers = 0;
        enemy_score = 0;
        my_answers = 0;
        my_score = 0;

        onYouLoseCard = null
        onEnemyLoseCard = null

        removeListeners()


    }

    fun removeListeners() {
        for (l in listeners) {
            l.key.removeEventListener(l.value)
        }
        listeners.clear()
    }

    override fun setRelation(relation: Relation,userId: String) {
        databaseReference.root.child(USERS_LOBBIES).child(userId).child(relation.id).child(FIELD_RELATION).setValue(relation)
    }

    override fun createLobby(lobby: Lobby, onFind: () -> (Unit)) {
        val lobbyId: String? = databaseReference.push().key

        lobbyId?.let {
            lobby.id = lobbyId
            databaseReference.child(it).setValue(lobby)
            currentUser?.let {
                it.id?.let { it1 ->
                    databaseReference.root.child(UserRepositoryImpl.TABLE_NAME).child(it1).child(FIELD_LOBBY_ID).setValue(lobbyId)
                    val relation:Relation = Relation()
                    relation.id = lobbyId
                    relation.relation = ONLINE_STATUS
                    databaseReference.root.child(USERS_LOBBIES).child(it1).child(lobbyId).setValue(relation)
                }
                it.lobbyId = lobbyId
            }

        }
        onFind()
    }

    override fun updateLobby(lobby: Lobby): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            databaseReference.child(lobby.id).setValue(lobby).addOnCompleteListener {
                e.onSuccess(true)
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun removeFastLobby(userId: String, lobby: Lobby): Single<Boolean> {
        Log.d(TAG_LOG,"remove fast lobby")
        val single: Single<Boolean> = Single.create { e ->

            val lobbyId: String? = lobby.id
            lobbyId?.let {
                databaseReference.child(it).removeValue()
                currentUser?.let {
                    it.id.let { it1 ->
                        databaseReference.root.child(USERS_LOBBIES).child(it1).child(lobbyId).removeValue()
                        databaseReference.root.child(USERS_LOBBIES).child(userId).child(lobbyId).removeValue()
                    }
                    e.onSuccess(true)
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun createFastLobby(userId: String, lobby: Lobby): Single<Lobby> {
        Log.d(TAG_LOG,"create fast lobby")
        val single: Single<Lobby> = Single.create { e ->

            val lobbyId: String? = databaseReference.push().key
            lobbyId?.let {
                lobby.id = lobbyId
                databaseReference.child(it).setValue(lobby)
                currentUser?.let {
                    it.id.let { it1 ->
                        val relation:Relation = Relation()
                        relation.id = lobbyId
                        relation.relation = ONLINE_STATUS
                        databaseReference.root.child(USERS_LOBBIES).child(it1).child(lobbyId).setValue(relation)
                        relation.relation = IN_GAME_STATUS
                        databaseReference.root.child(USERS_LOBBIES).child(userId).child(lobbyId).setValue(relation)
                    }
                    val gameData: GameData = GameData()
                    gameData.enemyId = userId
                    gameData.gameMode = ONLINE_GAME
                    gameData.role = FIELD_CREATOR
                    it.gameLobby = lobby
                    it.gameLobby?.gameData = gameData
                    setLobbyRefs(lobbyId)
                    e.onSuccess(lobby)
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun refuseGame(lobby: Lobby): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            Log.d(TAG_LOG, "refuse game")
            val relation: Relation = Relation()
            relation.id = lobby.id
            relation.relation = NOT_ACCEPTED
            lobby.gameData?.enemyId?.let {
                Log.d(TAG_LOG,"refuse in db")
                databaseReference.root.child(USERS_LOBBIES).child(it).child(lobby.id).setValue(relation)
            }
            currentUser.let {
                databaseReference.root.child(USERS_LOBBIES).child(it.id).child(lobby.id).setValue(null).addOnCompleteListener{ e.onSuccess(true)}

            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun waitEnemy(): Single<Relation> {
        val single: Single<Relation> = Single.create{ e ->
            val user: User? = currentUser
            user?.id?.let {
                val query: Query = databaseReference.root.child(USERS_LOBBIES).child(it)
                query.addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for(snap in p0.children) {
                            val relation: Relation? = snap.getValue(Relation::class.java)
                            relation?.let {
                                if(it.relation.equals(IN_GAME_STATUS)) {
                                    Log.d(TAG_LOG,"wait enemy in game")
                                    user.status = IN_GAME_STATUS
                                    query.removeEventListener(this)
                                    e.onSuccess(relation)
                                    /*findLobby(relation.id).subscribe { lobby ->
                                        query.removeEventListener(this)
                                        e.onSuccess(lobby)
                                    }*/
                                }
                                if(it.relation.equals(NOT_ACCEPTED)) {
                                    Log.d(TAG_LOG,"enemy refused")
                                    query.removeEventListener(this)
                                    e.onSuccess(relation)
                                }
                            }
                        }
                    }

                })
            }

        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun disconnectMe(): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            Log.d(TAG_LOG, "disconnect me")
            val myConnect = databaseReference.root.child(UserRepositoryImpl.TABLE_NAME).child(currentId).child(UserRepositoryImpl.FIELD_STATUS)
            myConnect.setValue(OFFLINE_STATUS)
            e.onSuccess(true)
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun findLobby(lobbyId: String): Single<Lobby> {
        val single: Single<Lobby> = Single.create{ e ->
            val query: Query = databaseReference.child(lobbyId)
            query.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()) {
                        Log.d(TAG_LOG,"lobby finded")
                        val lobby: Lobby? = p0.getValue(Lobby::class.java)
                        lobby?.let {
                            e.onSuccess(lobby)
                        }
                    } else {
                        Log.d(TAG_LOG,"lobby not exist")
                    }
                }

            })


        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun goToLobby(lobby: Lobby, onFind: () -> (Unit), onNotAccepted: () -> (Unit)) {

        setLobbyRefs(lobby.id)

        val playerData = LobbyPlayerData()
        playerData.playerId = currentId
        playerData.online = true

        myLobbyRef!!.setValue(playerData)

        val relation: Relation = Relation()
        relation.id = lobby.id
        relation.relation = ONLINE_STATUS
        lobby.creator?.playerId?.let {
            currentUser?.let { user ->
                databaseReference.root.child(USERS_LOBBIES).child(user.id).child(lobby.id).setValue(relation)
            }
            relation.relation = IN_GAME_STATUS
            databaseReference.root.child(USERS_LOBBIES).child(it).child(lobby.id).setValue(relation)
        }

        databaseReference.root.child(USERS_LOBBIES).child(currentId).child(lobby.id).addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snap: DataSnapshot) {
                val relation: Relation? = snap.getValue(Relation::class.java)
                relation?.let {
                    if(it.relation.equals(IN_GAME_STATUS)) {
                        currentUser?.let { it1 ->
                            it1.status = IN_GAME_STATUS
                            userRepository.changeUserStatus(it1).subscribe() }
                        onFind()
                    } else if(it.relation.equals(NOT_ACCEPTED)) {
                        onNotAccepted()
                    }
                }
            }

        })
    }

    override fun notAccepted(lobby: Lobby) {
        currentUser?.let {
            Log.d(TAG_LOG,"not accept")
            databaseReference.root.child(USERS_LOBBIES).child(it.id).child(lobby.id).setValue(null)
            myLobbyRef.setValue(null)

        }
    }

    override fun acceptMyGame(lobby: Lobby): Single<Boolean> {
        return Single.create { e ->
            val relation: Relation = Relation()
            relation.id = lobby.id
            relation.relation = IN_GAME_STATUS
            lobby.gameData?.enemyId?.let {
                databaseReference.root.child(USERS_LOBBIES).child(it).child(lobby.id).setValue(relation)
                e.onSuccess(true)
            }
        }
    }

    override fun cancelSearchGame(onCanceled: () -> (Unit)) {
        creatorLobbyRef!!.child(LobbyPlayerData.PARAM_online).onDisconnect().cancel()
        //TODO for parent ?

        nowSearchingDbRef!!.removeValue().addOnSuccessListener { onCanceled() }
        currentLobbyRef!!.removeValue()
    }

    fun getPlayerId(): String? {
        return currentId
    }

    private fun trackEnemyCards() {
        val enemyCardListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                lastEnemyChoose = dataSnapshot.getValue(CardChoose::class.java)!!
                callbacks?.onEnemyCardChosen(lastEnemyChoose!!)
            }

            override fun onChildRemoved(p0: DataSnapshot) {}
        }
        val enemyCardRef = enemyLobbyRef.child(LobbyPlayerData.PARAM_choosedCards)
        enemyCardRef.addChildEventListener(enemyCardListener)
    }

    private fun trackEnemyAnswers(lobby: Lobby) {
        enemyLobbyRef.child(LobbyPlayerData.PARAM_answers)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

                override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    val correct = dataSnapshot.value as Boolean
                    callbacks?.onEnemyAnswered(correct)
                    enemy_answers++
                    if (correct) {
                        enemy_score++
                    }
                    Log.d(TAG_LOG,"enemyAnswers = $enemy_answers")
                    Log.d(TAG_LOG,"enemyScroe = $enemy_score")
                    checkGameEnd(lobby)
                }

                override fun onChildRemoved(p0: DataSnapshot) {}
            })
    }

    private fun trackMyConnection() {
        val myConnect = databaseReference.root.child(UserRepositoryImpl.TABLE_NAME)
            .child(currentId).child(UserRepositoryImpl.FIELD_STATUS)
        val myConnectListener = object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(OFFLINE_STATUS.equals(snapshot.value)) {
                    Log.d(TAG_LOG,"my disconnect")
                    myConnect.removeEventListener(this)
                    onDisconnectAndLose(true)
                }
            }

        }
        myConnect.addValueEventListener(myConnectListener)
        listeners.put(myConnect, myConnectListener)
        myConnect.onDisconnect().setValue(OFFLINE_STATUS)
    }

    private fun trackEnemyConnection(lobby: Lobby) {
        lobby.gameData?.enemyId?.let {
            val enemyConnect = databaseReference.root.child(UserRepositoryImpl.TABLE_NAME).child(it)
                .child(UserRepositoryImpl.FIELD_STATUS)
            val enemyConnectListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && OFFLINE_STATUS.equals(snapshot.value)) {
                        Log.d(TAG_LOG, "enemy disconnect")
                        enemyConnect.removeEventListener(this)
                        onEnemyDisconnectAndYouWin(lobby)
                    }
                }
            }
            enemyConnect.addValueEventListener(enemyConnectListener)
            listeners.put(enemyConnect, enemyConnectListener)
        }
    }

    override fun startGame(lobby: Lobby, callbacks: InGameCallbacks) {
        this.callbacks = callbacks
        selectOnLoseCard(lobby)
        trackEnemyCards()
        trackEnemyAnswers(lobby)
        myLobbyRef.child(FIELD_ONLINE).setValue(true)
        trackMyConnection()
        trackEnemyConnection(lobby)
    }

    private fun selectOnLoseCard(lobby: Lobby) {

        lobby.gameData?.enemyId?.let {
            cardRepository.findMyCardsByEpoch(it, lobby.epochId).subscribe { enemyCards: List<Card>? ->
                cardRepository.findMyCardsByEpoch(currentId, lobby.epochId).subscribe { myCards: List<Card>? ->
                    onYouLoseCard = ArrayList(myCards).minus(enemyCards!!).getRandom()
                    if(onYouLoseCard == null) {
                        onYouLoseCard = myCards?.getRandom()
                    }

                    Log.d(TAG_LOG,"onYouLoseCard = ${onYouLoseCard?.id}")
                    //TODO если нет подходящей карты
                    // возможно стоит обрабатывать уже при входе в лобби

                    myLobbyRef
                        .child(LobbyPlayerData.PARAM_randomSendOnLoseCard)
                        .setValue(onYouLoseCard!!.id)
                }
            }
        }

        enemyLobbyRef!!
            .child(LobbyPlayerData.PARAM_randomSendOnLoseCard)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        cardRepository
                            .readCard(dataSnapshot.value as String)
                            .subscribe { t: Card? ->
                                onEnemyLoseCard = t!!
                            }
                    }
                }
            })

    }

    override fun chooseNextCard(lobby: Lobby,cardId: String) {
        lastMyChosenCardId = cardId;
        cardRepository.readCard(cardId).subscribe { card: Card? ->

            val questionId = card!!.test.questions.getRandom()!!.id


            val choose = CardChoose(cardId, questionId!!)

//            creatorLobbyRef!!.child(LobbyPlayerData.PARAM_choosedCards).push().setValue(choose)
            myLobbyRef.child(LobbyPlayerData.PARAM_choosedCards).push().setValue(choose)

        }
    }

    override fun answerOnLastQuestion(lobby: Lobby, correct: Boolean) {
        val query: Query =  myLobbyRef
            .child(LobbyPlayerData.PARAM_choosedCards)
            .orderByKey()
            .limitToLast(1)

        my_answers++
        if (correct) {
            my_score++
        }
        Log.d(TAG_LOG,"myAnswers = $my_answers")
        Log.d(TAG_LOG,"myScore = $my_score")

        checkGameEnd(lobby)

        query.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val key = dataSnapshot.key!!

                myLobbyRef.child(LobbyPlayerData.PARAM_answers)
                    .child(key)
                    .setValue(correct)

                query.removeEventListener(this)
            }

            override fun onChildRemoved(p0: DataSnapshot) {}
        })

    }

    private fun checkGameEnd(lobby: Lobby) {
        if (enemy_answers == lobby.cardNumber && my_answers == lobby.cardNumber) {
            Log.d("Alm", "repo: GAME END!!!")

            Log.d("Alm", "repo: GAME END onEnemyLoseCard: " + onEnemyLoseCard!!.id)
            Log.d("Alm", "repo: GAME END onYouLoseCard: " + onYouLoseCard!!.id)
            changeGameMode(MODE_END_GAME).subscribe{ changed ->
                waitGameMode(MODE_END_GAME).subscribe{ waited ->
                    //TODO
                    Log.d(TAG_LOG,"gameEnd and removeLobby")
                    removeLobbyAndRelations(lobby)

                    if (my_score > enemy_score) {
                        onWin(lobby)

                    } else if (enemy_score > my_score) {
                        onLose()

                    } else {
                        //TODO
                        compareLastCards(lobby)
                    }
                }
            }

        }
    }

    private fun removeLobbyAndRelations(lobby: Lobby) {
        if(lobby.isFastGame) {
            currentLobbyRef.setValue(null)
            val reference: DatabaseReference = databaseReference.root.child(USERS_LOBBIES).child(currentId)
            reference.child(lobby.id).setValue(null)
        }
        if(!lobby.creator?.playerId.equals(currentId)) {
            val reference: DatabaseReference = databaseReference.root.child(USERS_LOBBIES).child(currentId)
            reference.child(lobby.id).setValue(null)
        } else {
            val playerData: LobbyPlayerData = LobbyPlayerData()
            playerData.online = true
            playerData.playerId = currentId
            myLobbyRef.setValue(playerData)
            enemyLobbyRef.setValue(null)
            databaseReference.root.child(USERS_LOBBIES).child(currentId).child(lobby.id).setValue(null)
        }
    }

    override fun removeRedundantLobbies(shouldFind: Boolean): Single<Boolean> {
        Log.d(TAG_LOG,"removeRedundantLobbies")
        var single: Single<Boolean> = Single.create { e -> e.onSuccess(true)}
            if(userInSession) {
            currentUser.let { user ->
                single = Single.create { e ->
                    databaseReference.root.child(USERS_LOBBIES).child(user.id).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(!snapshot.exists()) {
                                e.onSuccess(true)
                            }
                            for (snap in snapshot.children) {
                                val relation: Relation? = snap.getValue(Relation::class.java)
                                if(relation == null) {
                                    e.onSuccess(true)
                                }
                                relation?.let {
                                    if (user.lobbyId.equals(it.id)) {
                                        val playerData: LobbyPlayerData = LobbyPlayerData()
                                        playerData.online = true
                                        playerData.playerId = currentId
                                        if (user.gameLobby == null) {
                                            user.gameLobby = Lobby()
                                            user.gameLobby?.gameData?.role = FIELD_CREATOR

                                        }
                                        setLobbyRefs(it.id)
                                        myLobbyRef.setValue(playerData)
                                        enemyLobbyRef.setValue(null)
                                    }
                                    Log.d(TAG_LOG, "removeLobby in users_lobbies")
                                    databaseReference.root.child(USERS_LOBBIES).child(user.id).child(it.id)
                                        .setValue(null).addOnCompleteListener { e.onSuccess(true) }
                                    if (shouldFind) {
                                        findLobby(it.id).subscribe { lobby ->
                                            if (lobby.isFastGame) {
                                                databaseReference.child(lobby.id).setValue(null)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    })
                }

                }
            }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun waitGameMode(mode: String): Single<Boolean> {
        val single: Single<Boolean> = Single.create{ e ->
            enemyLobbyRef.child(FIELD_MODE).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        val modeStr: String = snapshot.value as String
                        if (mode.equals(modeStr)) {
                            Log.d(TAG_LOG, "modeStr = $modeStr")
                            e.onSuccess(true)
                        }
                    }
                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    override fun changeGameMode(mode: String): Single<Boolean> {
        val single: Single<Boolean> = Single.create{ e ->
            myLobbyRef.child(FIELD_MODE).setValue(mode)
            e.onSuccess(true)
        }
        return single.compose(RxUtils.asyncSingle())
    }

    private fun compareLastCards(lobby: Lobby) {
        cardRepository
            .readCard(lastMyChosenCardId!!).subscribe { myLastCard: Card? ->
                cardRepository
                    .readCard(lastEnemyChoose!!.cardId).subscribe { enemyLastCard: Card? ->
                        var c = 0

                        c += compareCardsParameter({ card -> card.intelligence!! }, myLastCard!!, enemyLastCard!!)
                        c += compareCardsParameter({ card -> card.support!! }, myLastCard!!, enemyLastCard!!)
                        c += compareCardsParameter({ card -> card.prestige!! }, myLastCard!!, enemyLastCard!!)
                        c += compareCardsParameter({ card -> card.hp!! }, myLastCard!!, enemyLastCard!!)
                        c += compareCardsParameter({ card -> card.strength!! }, myLastCard!!, enemyLastCard!!)

                        if (c > 0) {
                            onWin(lobby)
                        } else if (c < 0) {
                            onLose()
                        } else {
                            val flag = Random.nextBoolean()
                            if (flag) onWin(lobby) else onLose()
//                            onDraw()
                        }

                    }
            }
    }

    private fun onDraw() {
        callbacks!!.onGameEnd(GameEndType.DRAW, onYouLoseCard!!)
    }

    fun compareCardsParameter(f: ((card: Card) -> Int), card1: Card, card2: Card): Int {
        return f(card1).compareTo(f(card2))
    }

    private fun onWin(lobby: Lobby) {
        moveCardAfterWin(lobby)
        callbacks!!.onGameEnd(GameEndType.YOU_WIN, onEnemyLoseCard!!)
        removeListeners()

    }

    private fun onLose() {
        callbacks!!.onGameEnd(GameEndType.YOU_LOSE, onYouLoseCard!!)
        removeListeners()

    }

    override fun watchMyStatus() {
        val myConnect = databaseReference.root.child(UserRepositoryImpl.TABLE_NAME).child(currentId).child(UserRepositoryImpl.FIELD_STATUS)
        myConnect.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(OFFLINE_STATUS.equals(snapshot.value)) {
                    Log.d(TAG_LOG,"my disconnect")
                    removeRedundantLobbies(true).subscribe()
                }
            }

        })
        myConnect.onDisconnect().setValue(OFFLINE_STATUS)
    }

    override fun onDisconnectAndLose(shouldFind: Boolean) {
        callbacks!!.onGameEnd(GameEndType.YOU_DISCONNECTED_AND_LOSE, onYouLoseCard!!)
        removeRedundantLobbies(shouldFind).subscribe()
        removeListeners()
    }

    override fun onEnemyDisconnectAndYouWin(lobby: Lobby) {
        moveCardAfterWin(lobby)
        callbacks!!.onGameEnd(GameEndType.ENEMY_DISCONNECTED_AND_YOU_WIN, onEnemyLoseCard!!)
        removeRedundantLobbies(true).subscribe()
        removeListeners()

    }

    private fun moveCardAfterWin(lobby: Lobby) {
        lobby.gameData?.enemyId?.let {
            cardRepository.addCardAfterGame(onEnemyLoseCard!!.id!!, getPlayerId()!!, it)
                .subscribe { e ->
                    Log.d(TAG_LOG, "card added after game")

                }
            getPlayerId()?.let { it1 ->
                if(!lobby.usersIds.contains(it1)) {
                    lobby.usersIds.add(it1)
                }
                updateLobby(lobby).subscribe()
                userEpochRepository.updateAfterGame(lobby, getPlayerId(), true, my_score).subscribe()
                userEpochRepository.updateAfterGame(lobby, it, false, enemy_score).subscribe()
            }
        }
    }

    override fun findOfficialTests(userId: String): Single<List<Lobby>> {
        return findTestsByType(userId)
    }

    override fun findOfficialTestsByQuery(query: String, userId: String): Single<List<Lobby>> {
        return findTestsByTypeByQuery(query, userId, OFFICIAL_TYPE)
    }

    fun findTestsByType(userId: String): Single<List<Lobby>> {
        Log.d(TAG_LOG, "findGames")
        val query: Query = databaseReference
        val single: Single<List<Lobby>> = Single.create { e ->
            val cardSingle: Single<List<Card>> = cardRepository.findMyCards(userId)
            cardSingle.subscribe{ myCards ->
                val myNumber = myCards.size
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val cards: MutableList<Lobby> = ArrayList()
                        for (snapshot in dataSnapshot.children) {
                            val card = snapshot.getValue(Lobby::class.java)
                            card?.let {
                                if (ONLINE_STATUS.equals(card.status) && !card.isFastGame
                                    && (myNumber >= card.cardNumber || card.id.equals(AppHelper.currentUser.lobbyId))) {
                                    if(card.id.equals(AppHelper.currentUser.lobbyId)) {
                                        it.isMyCreation = true
                                    }
                                    cards.add(it)
                                }
                            }
                        }
                        Log.d(TAG_LOG, "succes games")
                        e.onSuccess(cards)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findTestsByTypeByQuery(userQuery: String, userId: String, type: String): Single<List<Lobby>> {
        val queryPart = userQuery.toLowerCase()
        var query = databaseReference.orderByChild(FIELD_LOWER_TITLE).startAt(queryPart).endAt(queryPart + QUERY_END)
        val single: Single<List<Lobby>> = Single.create { e ->
            val cardSingle: Single<List<Card>> = cardRepository.findMyCards(userId)
            cardSingle.subscribe { myCards ->
                val myNumber = myCards.size
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val cards: MutableList<Lobby> = ArrayList()
                        for (snapshot in dataSnapshot.children) {
                            val card = snapshot.getValue(Lobby::class.java)
                            card?.let {
                                if ((ONLINE_STATUS.equals(card.status) && card.type.equals(type)) && !card.isFastGame
                                    && (myNumber >= card.cardNumber || card.id.equals(AppHelper.currentUser.lobbyId))) {
                                    if(it.id.equals(AppHelper.currentUser.lobbyId)) {
                                        it.isMyCreation = true
                                    }
                                    cards.add(it)
                                }
                            }

                        }
                        e.onSuccess(cards)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })

            }
        }
        return single.compose(RxUtils.asyncSingle())
    }


    //


    interface InGameCallbacks {
        fun onGameEnd(type: GameEndType, card: Card)

        fun onEnemyCardChosen(choose: CardChoose)
        fun onEnemyAnswered(correct: Boolean)
    }

    enum class GameEndType {
        YOU_WIN, YOU_LOSE, YOU_DISCONNECTED_AND_LOSE, ENEMY_DISCONNECTED_AND_YOU_WIN,
        DRAW
    }


    companion object {
        val ROUNDS_COUNT = 3

        const val TABLE_LOBBIES = "lobbies"
        const val USERS_LOBBIES = "users_lobbies"


        const val FIELD_RELATION = "relation"

        const val FIELD_INVITED = "invited"
        const val FIELD_CREATOR = "creator"

        const val FIELD_MODE = "mode"
        const val FIELD_ONLINE = "online"

    }
}