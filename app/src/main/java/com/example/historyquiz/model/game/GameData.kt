package com.example.historyquiz.model.game

import com.example.historyquiz.model.card.Card
import com.example.historyquiz.repository.game.GameRepositoryImpl.Companion.FIELD_CREATOR
import com.example.historyquiz.utils.Const.BOT_GAME

class GameData {

    var lastEnemyChoose: CardChoose? = null
    var lastMyChosenCard: CardChoose? = null

    lateinit var enemyId: String
    var gameMode: String = BOT_GAME
    var role: String = FIELD_CREATOR

    var enemy_answers = 0;
    var enemy_score = 0;

    var my_answers = 0;
    var my_score = 0;

    var onYouLoseCard: Card? = null
    var onEnemyLoseCard: Card? = null
}