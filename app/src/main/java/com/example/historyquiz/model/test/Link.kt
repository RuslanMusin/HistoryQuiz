package com.example.historyquiz.model.test

class Link() {

    constructor(name: String, url: String, content: String) : this() {
        this.name = name
        this.url = url
        this.content = content
    }

    lateinit var name: String

    lateinit var url: String

    lateinit var content: String
}