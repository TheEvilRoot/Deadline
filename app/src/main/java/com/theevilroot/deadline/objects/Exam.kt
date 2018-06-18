package com.theevilroot.deadline.objects

import java.util.*

class Exam(val examName: String, val examDate: Long) {
    fun state():Boolean {
        return examDate > Date().time
    }
    var selected: Boolean = false

    override fun equals(other: Any?): Boolean {
        if(other !is Exam)
            return false
        val o = other as Exam
        return o.examName == this.examName || o.examDate == this.examDate
    }

}