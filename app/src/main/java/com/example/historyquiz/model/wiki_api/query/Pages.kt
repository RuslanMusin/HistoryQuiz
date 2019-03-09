package com.example.historyquiz.model.wiki_api.query

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList

class Pages {

    @field: ElementList(inline = true, required = false) var pages: List<Page>? = null
}
