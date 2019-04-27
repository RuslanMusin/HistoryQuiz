package com.example.historyquiz.repository.card

import com.example.historyquiz.model.card.Card
import io.reactivex.Single

interface CardRepository {

    fun toMap(card: Card?): Map<String, Any?>

    fun toMapId( value: String?): Map<String, Any?>

    fun readCard(cardId: String): Single<Card>

    fun readCardForTest(cardId: String): Single<Card>

    fun findCards(cardsIds: List<String>): Single<List<Card>>

    fun findMyCards(userId: String): Single<List<Card>>

    fun findMyCardsByEpoch(userId: String, epochId: String): Single<List<Card>>

    fun findMyAbstractCardStates(abstractCardId: String, userId: String): Single<List<Card>>

    fun addCardAfterGame(cardId: String , winnerId: String, loserId: String): Single<Boolean>
}