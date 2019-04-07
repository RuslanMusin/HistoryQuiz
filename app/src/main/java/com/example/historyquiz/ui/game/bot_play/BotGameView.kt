package com.example.historyquiz.ui.game.bot_play

import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.test.Question
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.game.GameRepositoryImpl
import com.example.historyquiz.ui.game.play.PlayView

interface BotGameView : PlayView {
    fun setEnemyUserData(user: User)

    fun setCardsList(cards: ArrayList<Card>)

    fun changeCards(cards: MutableList<Card>, mutCards: MutableList<Card>)

    fun setCardChooseEnabled(enabled: Boolean)

    fun showEnemyCardChoose(card: Card)
    fun hideEnemyCardChoose()

    fun showQuestionForYou(question: Question)
    fun hideQuestionForYou()

    fun showYouCardChoose(choose: Card)//CardChooose? чтобы видеть вопрос для противника
    fun hideYouCardChoose()

    fun showEnemyAnswer(correct: Boolean)
    fun showYourAnswer(correct: Boolean)

    fun showGameEnd(type: GameRepositoryImpl.GameEndType, card: Card)
}