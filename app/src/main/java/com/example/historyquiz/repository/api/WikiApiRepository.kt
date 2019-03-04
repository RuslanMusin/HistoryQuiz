package com.example.historyquiz.repository.api

import com.example.historyquiz.model.wiki_api.opensearch.Item
import com.example.historyquiz.model.wiki_api.query.Page
import io.reactivex.Single


interface WikiApiRepository {

    fun opensearch(query: String): Single<List<Item>>

    fun query(query: String): Single<List<Page>>
}