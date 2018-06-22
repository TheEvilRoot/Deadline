package com.theevilroot.deadline.objects

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

class Preference(val isGroup: Boolean, val title: String = "", val name: String = "", val description: String = "",val type: PreferenceType<out Any> = PT_BOOLEAN, var value:String = "", val id: String = "")
