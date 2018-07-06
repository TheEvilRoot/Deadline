package com.theevilroot.deadline.utils

import com.google.gson.*
import com.theevilroot.deadline.TheHolder
import com.theevilroot.deadline.objects.*
import java.io.File
import java.util.*

fun handlePreferenceFile(json: JsonElement): JsonObject? {
    if(json.isJsonArray)
        return assimilatePreference(json.asJsonArray)
    if(!json.isJsonObject)
        return null
    val obj = json.asJsonObject
    if(!obj.has("version"))
        return if(obj.has("preferences") && obj["preferences"].isJsonArray)
            assimilatePreference(obj["preferences"].asJsonArray)
        else
            null
    else
        if(obj["version"].asInt == TheHolder.preferenceVersion && obj["preferences"].asJsonArray.size() == TheHolder.userPreferences.size)
            return obj
        else
            return assimilatePreference(obj["preferences"].asJsonArray)
}

fun assimilatePreference(json: JsonArray):JsonObject {
    val ret = JsonObject()
    ret.addProperty("version", TheHolder.preferenceVersion)
    val array = JsonArray()
    val user = json.map { Preference.fromJson(it.asJsonObject) }
    TheHolder.userPreferences.forEach {current ->
        with(user.filter { it.id == current.id }.getOrNull(0)) {
            if(this != null) {
                array.add(current.apply {
                    this.value = this@with.value
                }.toJson())
            }else{
                array.add(current.toJson())
            }
        }
    }
    ret.add("preferences", array)
    return ret
}

fun parsePreferences(file: File): List<Preference> {
    val json = handlePreferenceFile(JsonParser().parse(file.readText()))
            ?: return writeAndGetPreferences(file)
    return json["preferences"].asJsonArray.map { it.asJsonObject }.map { Preference.fromJson(it) }
}

fun parseValue(value: JsonObject): PreferenceValue =
        when(value["type"].asString) {
            PreferenceBoolean::class.java.simpleName ->
                PreferenceBoolean(value["stringValue"].asString!!.toBoolean())
            PreferenceInt::class.java.simpleName ->
                PreferenceInt(value["stringValue"].asString!!.toIntOrNull() ?: throw IllegalPreferenceTypeException("Unparseable value '$value'"))
            PreferenceFloat::class.java.simpleName ->
                PreferenceFloat(value["stringValue"].asString!!.toFloatOrNull() ?: throw IllegalPreferenceTypeException("Unparseable value '$value'"))
            PreferenceString::class.java.simpleName ->
                PreferenceString(value["stringValue"].asString)
            PreferenceDate::class.java.simpleName ->
                PreferenceDate(Date(value["stringValue"].asString.toLongOrNull() ?: throw IllegalPreferenceTypeException("Unparsable value '$value'")), value["pattern"].asString)
            else ->
                throw IllegalPreferenceTypeException("Unsupported value type found on $value")
        }

fun writePreferences(file: File) {
    val json = JsonObject()
    with(json){
        addProperty("version", TheHolder.preferenceVersion)
        val prefs = JsonArray()
        TheHolder.userPreferences.map(Preference::toJson).forEach { prefs.add(it) }
        add("preferences", prefs)
    }
    file.writeText(GsonBuilder().setPrettyPrinting().create().toJson(json))
}

fun writeAndGetPreferences(file: File): List<Preference> {
    file.createNewFile()
    writePreferences(file)
    return TheHolder.userPreferences
}