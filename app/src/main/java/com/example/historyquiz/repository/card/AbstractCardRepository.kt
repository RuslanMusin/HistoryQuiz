package com.example.historyquiz.repository.card

import com.example.historyquiz.model.card.AbstractCard
import io.reactivex.Single

interface AbstractCardRepository {

    fun toMap(card: AbstractCard?): Map<String, Any?>

    fun toMapId(value: String?): Map<String, Any?>

    fun findAbstractCardId(wikiUrl: String?): Single<String>

    fun findAbstractCard(cardId: String?): Single<AbstractCard>

    fun findAbstractCards(cardsIds: List<String>): Single<List<AbstractCard>>

    fun findMyAbstractCards(userId: String): Single<List<AbstractCard>>

    fun findMyAbstractCardsByQuery(queryPart: String, userId: String): Single<List<AbstractCard>>


}