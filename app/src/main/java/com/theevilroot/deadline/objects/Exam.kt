package com.theevilroot.deadline.objects

import java.util.*

class Exam(val examName: String, val examDate: Long) {
    fun state():Boolean {
        return examDate > Date().time
    }
    var selected: Boolean = false

}