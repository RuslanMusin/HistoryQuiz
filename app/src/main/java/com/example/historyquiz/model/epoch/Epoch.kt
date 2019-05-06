package com.example.historyquiz.model.epoch

class Epoch {

    lateinit var id: String
    lateinit var name: String

    constructor()

    constructor(name: String) {
        this.name = name
    }
}