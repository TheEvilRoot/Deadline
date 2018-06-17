package com.theevilroot.deadline.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.theevilroot.deadline.TheHolder
import java.util.*

class TimerService: Service() {

    inner class TickRunnable: Runnable {
        var willStopped = false
        override fun run() {
            val tick = onTick()
            if(tick.first && !willStopped) {
                handler.postDelayed(this, 1000)
            }else {
                dispatchCallback(CallbackType.STOP, Bundle().apply { this.putString("why", tick.second) })
            }
        }
    }

    private val binder = LocalBinder()
    private val handler = Handler()
    private var callbackListener: CallbackListener? = null
    private val runnable = TickRunnable()

    var isTimerActive = false

    private fun stopTimer() {
        runnable.willStopped = true
    }

    fun startTimer() {
        handler.postDelayed(runnable, 0)
        dispatchCallback(CallbackType.START, Bundle.EMPTY)
    }

    fun onTick(): Pair<Boolean, String> {
        try {
            val currentTime = Date().time
            val nextExam = TheHolder.examList.sortedBy { it.examDate }.firstOrNull {
                Log.i("TIME", "${it.examDate} ${if(it.examDate > currentTime) ">" else "<"} $currentTime")
                it.examDate > currentTime
            } ?: return false to "Нету больше экзаменов!"
            val bundle = Bundle()
            val time = nextExam.examDate - currentTime
            val hours = ((time / (1000*60*60)) % 24)
            val mins = ((time / (1000*60)) % 60)
            val secs = (time / 1000) % 60
            val days =  (time / (1000*60*60*24))
            bundle.putInt("days", days.toInt())
            bundle.putInt("hours",hours.toInt())
            bundle.putInt("minutes", mins.toInt())
            bundle.putInt("seconds", secs.toInt())
            bundle.putInt("exam", TheHolder.examList.indexOf(nextExam))
            dispatchCallback(CallbackType.TICK, bundle)
            return true to ""
        }catch (e: Exception) {
            e.printStackTrace()
            dispatchCallback(CallbackType.ERROR, Bundle.EMPTY)
            return false to "Error!"
        }
    }

    fun registerCallbackListener(listener: CallbackListener) {
        this.callbackListener = listener
    }

    private fun dispatchCallback(type: CallbackType, data: Bundle) {
         callbackListener?.callback(type, data)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(TheHolder.examList.isEmpty()) {
            Toast.makeText(this, "Exam list is empty. It's not necessary to start service!", Toast.LENGTH_SHORT).show()
            this.stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isTimerActive)
            stopTimer()
    }

    override fun onBind(intent: Intent?): IBinder = binder
    inner class LocalBinder: Binder() {
        fun getService(): TimerService = this@TimerService
    }

}