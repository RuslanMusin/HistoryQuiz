package com.example.historyquiz.ui.game.play

import com.example.historyquiz.ui.base.interfaces.BasicFunctional

interface PlayView: BasicFunctional {

    fun onAnswer(isRight: Boolean)
}