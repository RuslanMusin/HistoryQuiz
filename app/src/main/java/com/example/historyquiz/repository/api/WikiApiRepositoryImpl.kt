package com.example.historyquiz.repository.api

import com.example.historyquiz.api.services.WikiService
import com.example.historyquiz.model.wiki_api.opensearch.Item
import com.example.historyquiz.model.wiki_api.opensearch.SearchSuggestion
import com.example.historyquiz.model.wiki_api.opensearch.Section
import com.example.historyquiz.model.wiki_api.query.Api
import com.example.historyquiz.model.wiki_api.query.Page
import com.example.historyquiz.model.wiki_api.query.Pages
import com.example.historyquiz.model.wiki_api.query.Query
import com.example.historyquiz.utils.Const.ACTION_QUERY
import com.example.historyquiz.utils.Const.ACTION_SEARCH
import com.example.historyquiz.utils.Const.EXINTRO
import com.example.historyquiz.utils.Const.EXPLAINTEXT
import com.example.historyquiz.utils.Const.FORMAT
import com.example.historyquiz.utils.Const.NAMESPACE
import com.example.historyquiz.utils.Const.PILICENSE
import com.example.historyquiz.utils.Const.PIPROP
import com.example.historyquiz.utils.Const.PROP
import com.example.historyquiz.utils.RxUtils
import io.reactivex.Single
import javax.inject.Inject


class WikiApiRepositoryImpl @Inject constructor(): WikiApiRepository {

    @Inject
    lateinit var wikiService: WikiService

    override fun opensearch(query: String): Single<List<Item>> {
        return wikiService
            .opensearch(FORMAT, ACTION_SEARCH, query, NAMESPACE)
            .map<Section>(SearchSuggestion::section)
            .map<List<Item>>(Section::items)
            .compose(RxUtils.asyncSingle())
    }

    override fun query(query: String): Single<List<Page>> {
        return wikiService
            .query(FORMAT, ACTION_QUERY, PROP, EXINTRO, EXPLAINTEXT, PIPROP, PILICENSE, query)
            .map<Query>(Api::query)
            .map<Pages>(Query::pages)
            .map<List<Page>>(Pages::pages)
            .compose(RxUtils.asyncSingle())
    }
}
