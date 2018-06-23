package com.theevilroot.deadline.objects

import com.google.gson.JsonObject
import com.theevilroot.deadline.dateFormat
import com.theevilroot.deadline.timeFormat
import java.util.*

class PreferenceType<T>(val parse: (String) -> T?)

val PT_INT = PreferenceType(String::toIntOrNull)
val PT_STRING = PreferenceType({ it })
val PT_BOOLEAN = PreferenceType(String::toBoolean)
val PT_DATE = PreferenceType({ s ->
    try {
        dateFormat.parse(s)
    }catch (e: Exception) {
        null
    }
})
val PT_TIME = PreferenceType({ s ->
    try {
        timeFormat.parse(s)
    }catch (e: Exception) {
        null
    }
})

class Preference(val isGroup: Boolean, val title: String = "", val name: String = "", val description: String = "",val type: PreferenceType<out Any> = PT_BOOLEAN, var value:String = "", val id: String = "") {
    fun toJsonObject():JsonObject = JsonObject().apply{
        addProperty("isGroup", isGroup)
        if(isGroup) {
            addProperty("title", title)
        }else {
            addProperty("name", name)
            addProperty("description", description)
            addProperty("type", when (type) {
                PT_STRING -> "string"
                PT_BOOLEAN -> "boolean"
                PT_DATE -> "date"
                PT_TIME -> "time"
                PT_INT -> "int"
                else -> "string"
            })
            addProperty("value", value)
            addProperty("id", id)
        }
    }
}