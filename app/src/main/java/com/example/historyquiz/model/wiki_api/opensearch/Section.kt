package com.example.historyquiz.model.wiki_api.opensearch

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root
class Section {

    @field:ElementList(inline = true, required = false) var items: List<Item>? = null

    override fun toString(): String {
        return "ClassPojo [Item = $items]"
    }
}
