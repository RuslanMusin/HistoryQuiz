package com.example.historyquiz.ui.game.play

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.game.CardChoose
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.repository.game.GameRepositoryImpl
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BasePresenter
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const.BOT_GAME
import com.example.historyquiz.utils.Const.BOT_ID
import com.example.historyquiz.utils.Const.MODE_PLAY_GAME
import com.example.historyquiz.utils.Const.OFFICIAL_TYPE
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.getRandom
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

@InjectViewState
class PlayGamePresenter @Inject constructor() : BasePresenter<PlayGameView>(), GameRepositoryImpl.InGameCallbacks {

    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var cardRepository: CardRepository

    @Inject
    lateinit var userRepository: UserRepository

    lateinit var botCards: MutableList<Card>

    lateinit var lobby: Lobby

    fun setInitState(initlobby: Lobby) {
        lobby = initlobby
        gameRepository.setLobbyRefs(lobby.id)
        gameRepository.watchMyStatus()
        val single: Single<List<Card>>
        if(lobby.type.equals(OFFICIAL_TYPE)) {
            single = cardRepository.findMyCards(currentId)
        } else {
            single = cardRepository.findMyCards(currentId)
        }
        single.subscribe { cards: List<Card>? ->
            cards?.let {
                val mutCards = cards.toMutableList()
                val myCards: MutableList<Card> = ArrayList()

                for (i in 1..lobby.cardNumber) {
                    mutCards.getRandom()?.let {
                        Log.d(TAG_LOG,"random card num = $i and name = ${it.abstractCard?.name}")
                        myCards.add(it)
                        mutCards.remove(it)
                    }
                }
                if (cards.size > lobby.cardNumber) {
                    viewState.changeCards(myCards,mutCards)
                } else {
                    changeGameMode(MODE_PLAY_GAME)
                    setCardList(myCards, 20000)
                }
            }
        }
    }

    fun waitEnemyGameMode(mode: String): Single<Boolean> {
        Log.d(TAG_LOG,"wait mode  = $mode")
        return gameRepository.waitGameMode(mode)
    }

    fun changeGameMode(mode: String) {
        Log.d(TAG_LOG,"change mode = $mode")
        gameRepository.changeGameMode(mode).subscribe()
    }

    fun setCardList(myCards: List<Card>, time: Long) {
        Log.d(TAG_LOG,"set card list")
        viewState.waitEnemyTimer(time)
        waitEnemyGameMode(MODE_PLAY_GAME).subscribe { e ->
            viewState.setCardsList(ArrayList(myCards))
            viewState.setCardChooseEnabled(true)

            /* RepositoryProvider.userRepository.readUserById(gameRepository.enemyId!!)
                .subscribe { t: User? ->
                    viewState.setEnemyUserData(t!!)
                }*/
            lobby.gameData?.enemyId?.let {
                userRepository.readUserById(it)
                    .subscribe { t: User? ->
                        viewState.setEnemyUserData(t!!)
                    }
            }

            gameRepository.startGame(lobby, this)

            if (lobby.gameData?.gameMode.equals(BOT_GAME)) {
                Log.d(TAG_LOG, "find bot cards")
                val single: Single<List<Card>>
                if (lobby.type.equals(OFFICIAL_TYPE)) {
                    single = cardRepository.findMyCards(BOT_ID)
                } else {
                    single = cardRepository.findMyCards(BOT_ID)
                }
                single.subscribe { cards ->
                    gameRepository.selectOnBotLoseCard(cards)
                    botCards = cards.toMutableList()
                }
            }
        }
    }

    fun chooseCard(card: Card) {
        gameRepository.findLobby(lobby.id).subscribe { e ->
            viewState.setCardChooseEnabled(false)
            gameRepository.chooseNextCard(lobby, card.id!!)
            viewState.showYouCardChoose(card)
            youCardChosed = true
            if (lobby.gameData?.gameMode.equals(BOT_GAME)) {
                Log.d(TAG_LOG, "bot choose card")
                botChooseCard()
            }
        }

        /* if (enemyCardChosed) {
             showQuestion()
         }*/
    }

    fun botChooseCard() {
        val card: Card? = botCards.getRandom()
        botCards.remove(card)
        card?.let {
            //            viewState.showEnemyCardChoose(it)
            card.id?.let { it1 -> gameRepository.botNextCard(lobby, it1) }
        }
//        enemyCardChosed = true
//        showQuestion()

    }

    fun answer(correct: Boolean) {
        viewState.hideQuestionForYou()

        viewState.hideEnemyCardChoose()
        viewState.hideYouCardChoose()

        viewState.showYourAnswer(correct)

        gameRepository.findLobby(lobby.id).subscribe { e ->
            gameRepository.answerOnLastQuestion(lobby, correct)
            enemyCardChosed = false
            youCardChosed = false

            if (lobby.gameData?.gameMode.equals(BOT_GAME)) {
                Log.d(TAG_LOG, "bot answer")
                answerBot()
            }
        }


    }

    fun answerBot() {
        val correct: Boolean = Random().nextBoolean()
        gameRepository.botAnswer(lobby,correct)
    }

    override fun onGameEnd(type: GameRepositoryImpl.GameEndType, card: Card) {
        Log.d("Alm", "Game End: " + type)

        viewState.showGameEnd(type,card)

//        when(type){
//            GamesRepository.GameEndType.YOU_WIN->{
//                viewState.
//            }
//        }

    }

    var youCardChosed = false
    var enemyCardChosed = false

    var lastEnemyChoose: CardChoose? = null

    override fun onEnemyCardChosen(choose: CardChoose) {
        Log.d("Alm", "enemy chosen card " + choose.cardId)
        Log.d("Alm", "enemy chosen question " + choose.questionId)
        enemyCardChosed = true
        lastEnemyChoose = choose
        cardRepository.readCard(choose.cardId).subscribe { card ->
            viewState.showEnemyCardChoose(card)
        }
//        viewState.setCardChooseEnabled(true)
    }

    fun enemyDisconnected() {
        gameRepository.onEnemyDisconnectAndYouWin(lobby)
    }

    fun showQuestion() {
        gameRepository.findLobby(lobby.id).subscribe { e ->
            if (youCardChosed and enemyCardChosed) {
                cardRepository.readCard(lastEnemyChoose!!.cardId).subscribe { card ->
                    viewState.showQuestionForYou(card.test.questions
                        .first { q -> q.id == lastEnemyChoose!!.questionId })
                }
            }
        }

    }

    override fun onEnemyAnswered(correct: Boolean) {
        viewState.showEnemyAnswer(correct)
        viewState.setCardChooseEnabled(true)
    }
}