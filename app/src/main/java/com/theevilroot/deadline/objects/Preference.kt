package com.theevilroot.deadline.objects

import com.google.gson.JsonObject
import com.theevilroot.deadline.utils.parseValue

class Preference(val id: String = "",
                 val title: String = "Я ЕБАНУЛСЯ",
                 val name: String = "",
                 val description: String = "",
                 var value:PreferenceValue = PreferenceValue(Unit),
                 val isGroup: Boolean = false) {
    fun toJson(): JsonObject {
        val json = JsonObject()
        with(json) {
            if(isGroup) {
                addProperty("isGroup", isGroup)
                addProperty("title", title)
                return this
            }
            addProperty("id", id)
            addProperty("name", name)
            addProperty("description", description)
            val valueObj = JsonObject()
            valueObj.addProperty("type", value::class.java.simpleName)
            valueObj.addProperty("stringValue", when {
                value.isBoolean -> value.boolean.booleanValue.toString()
                value.isFloat -> value.float.floatValue.toString()
                value.isString -> value.string.stringValue
                value.isInt -> value.int.intValue.toString()
                value.isDate -> value.date.dateValue.time.toString()
                else -> value.value.toString()
            })
            if(value.isDate)
                valueObj.addProperty("pattern", value.date.pattern)
            add("value", valueObj)
        }
        return json
    }

    companion object {
        fun fromJson(it: JsonObject) =
                if(it.has("isGroup") && it["isGroup"].asBoolean) {
                    Preference(isGroup = true, title = it["title"].asString)
                } else {
                    Preference(id = it["id"].asString,
                            name = it["name"].asString,
                            description = it["description"].asString,
                            value = parseValue(it["value"].asJsonObject))
                }
    }
}