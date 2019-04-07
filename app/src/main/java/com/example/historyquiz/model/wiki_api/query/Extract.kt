package com.example.historyquiz.model.wiki_api.query

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Text

class Extract {

    @field:Text var content: String? = null

    @field:Attribute(required = false) var space: String? = null

    override fun toString(): String {
        return "ClassPojo [content = $content]"
    }
}
