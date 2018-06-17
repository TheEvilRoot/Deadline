package com.theevilroot.deadline.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

class TimerServiceConnection(private val onConnect: (TimerService) -> Unit, private val onDisconnect: () -> Unit): ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName?) {
        onDisconnect()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder) {
        onConnect((service as TimerService.LocalBinder).getService())
    }
}
