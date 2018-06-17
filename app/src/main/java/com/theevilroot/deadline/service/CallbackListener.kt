package com.theevilroot.deadline.service

import android.os.Bundle

enum class CallbackType {
    TICK, START, STOP, ERROR
}

interface CallbackListener {
    fun callback(type: CallbackType, data: Bundle)
}