package com.theevilroot.deadline.utils

import com.google.gson.JsonParser
import com.theevilroot.deadline.objects.Exam
import java.io.File

fun writeAndGetConfig(file: File): List<Exam> {
    file.createNewFile()
    file.writeText("[]")
    return parseConfig(file)
}


fun parseConfig(file: File): List<Exam> {
    val json = JsonParser().parse(file.readText()).asJsonArray
    return json.map { it.asJsonObject }.map { Exam(it["name"].asString, it["date"].asLong) }
}