package com.example.historyquiz.repository.game

import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.db_dop_models.Relation
import com.example.historyquiz.model.game.Lobby
import io.reactivex.Single

interface GameRepository {

    fun setLobbyRefs(lobbyId: String)

    fun removeLobby(id: String)

    fun joinBot(lobby: Lobby): Single<Boolean>

    fun setRelation(relation: Relation, userId: String)

    fun createLobby(lobby: Lobby, onFind: () -> (Unit))

    fun updateLobby(lobby: Lobby): Single<Boolean>

    fun removeFastLobby(userId: String, lobby: Lobby): Single<Boolean>

    fun createFastLobby(userId: String, lobby: Lobby): Single<Lobby>

    fun createBotLobby(lobby: Lobby, onFind: () -> (Unit))

    fun refuseGame(lobby: Lobby): Single<Boolean>

    fun waitEnemy(): Single<Relation>

    fun disconnectMe(): Single<Boolean>

    fun findLobby(lobbyId: String): Single<Lobby>

    fun goToLobby(lobby: Lobby, onFind: () -> (Unit), onNotAccepted: () -> (Unit))

    fun notAccepted(lobby: Lobby)

    fun acceptMyGame(lobby: Lobby): Single<Boolean>

    fun cancelSearchGame(onCanceled: () -> (Unit))

    fun startGame(lobby: Lobby, callbacks: GameRepositoryImpl.InGameCallbacks)

    fun selectOnBotLoseCard(cards: List<Card>)

    fun botNextCard(lobby: Lobby, cardId: String)

    fun answerOnLastQuestion(lobby: Lobby, correct: Boolean)

    fun botAnswer(lobby: Lobby, correct: Boolean)

    fun removeRedundantLobbies(shouldFind: Boolean)

    fun waitGameMode(mode: String): Single<Boolean>

    fun changeGameMode(mode: String): Single<Boolean>

    fun onDisconnectAndLose(shouldFind: Boolean)

    fun onEnemyDisconnectAndYouWin(lobby: Lobby)

    fun chooseNextCard(lobby: Lobby,cardId: String)

    fun findOfficialTests(userId: String): Single<List<Lobby>>

    fun findOfficialTestsByQuery(query: String, userId: String): Single<List<Lobby>>
}