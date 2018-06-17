package com.theevilroot.deadline.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.gson.JsonParser
import com.nipunbirla.boxloader.BoxLoaderView
import com.theevilroot.deadline.*
import com.theevilroot.deadline.objects.Exam
import com.theevilroot.deadline.service.CallbackListener
import com.theevilroot.deadline.service.CallbackType
import com.theevilroot.deadline.service.TimerService
import com.theevilroot.deadline.service.TimerServiceConnection
import java.io.File
import kotlin.concurrent.thread

class MainActivity: AppCompatActivity(), CallbackListener {

    val layout = R.layout.main_activity

    private val days: TextView by bind(R.id.days_view)
    private val minutes: TextView by bind(R.id.minutes_view)
    private val hours: TextView by bind(R.id.hours_view)
    private val seconds: TextView by bind(R.id.seconds_view)
    private val count: TextView by bind(R.id.count_view)
    private val label: TextView by bind(R.id.label_view)
    private val alert: TextView by bind(R.id.alert_view)
    private val loader: BoxLoaderView by bind(R.id.loader)
    private val examSettings:ImageButton by bind(R.id.exam_settings_button)
    private val settings: ImageButton by bind(R.id.settings_button)

    private val slideIn by load(android.R.anim.slide_in_left)
    private val slideOut by load(android.R.anim.slide_out_right)
    private val fadeIn by load(android.R.anim.fade_in)
    private val fadeOut by load(android.R.anim.fade_out)

    private var service: TimerService? = null
    private var serviceBound = false
    private val serviceConnection = TimerServiceConnection(onConnect = { srv ->
        serviceBound = true
        service = srv
        service!!.registerCallbackListener(this)
        service!!.startTimer()
    }, onDisconnect = {
        serviceBound = false
        service = null
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ThePreferences.secondBackgroundColor
        }
        settings.setColorFilter(ThePreferences.iconsColor)
        examSettings.setColorFilter(ThePreferences.iconsColor)
        examSettings.setOnClickListener {
            if(!TheHolder.isConfigLoaded)
                return@setOnClickListener
            val intent = Intent(this, ExamsActivity::class.java)
            startActivity(intent)
        }
        settings.setOnClickListener {
            val intent = Intent(this, ThemePreferencesActivity::class.java)
            startActivity(intent)
        }
        if(!TheHolder.isConfigLoaded)
            loadConfig()
        else
            startTimer()
    }

    private fun loadConfig() {
        loader.setSpeed(10)
        thread(true) {
            try {
                val file = File(filesDir, "config")
                if (!file.exists()) {
                    val exams = writeAndGet(file)
                    Thread.sleep(2000)
                    runOnUiThread { onLoad(exams) }
                    return@thread
                }
                val exams = parseConfig(file)
                Thread.sleep(2000)
                val themeFile = File(filesDir, "theme")
                if(themeFile.exists()) {
                    val json = JsonParser().parse(themeFile.readText()).asJsonObject
                    ThePreferences.activeColor = Color.parseColor(json["activeColor"].asString)
                    ThePreferences.iconsColor = Color.parseColor(json["iconsColor"].asString)
                    ThePreferences.backgroundColor = Color.parseColor(json["backgroundColor"].asString)
                    ThePreferences.inactiveColor = Color.parseColor(json["inactiveColor"].asString)
                    ThePreferences.secondBackgroundColor = Color.parseColor(json["secondBackgroundColor"].asString)
                    ThePreferences.selectionColor = Color.parseColor(json["selectionColor"].asString)
                    ThePreferences.textColor = Color.parseColor(json["textColor"].asString)
                }
                runOnUiThread { onLoad(exams) }
            }catch (e: Exception) {
                runOnUiThread { onError(e) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(TheHolder.needRefresh)
            startTimer()
    }

    private fun writeAndGet(file: File): List<Exam> {
        file.createNewFile()
        file.writeText("[]")
        return parseConfig(file)
    }

    private fun parseConfig(file: File): List<Exam> {
        val json = JsonParser().parse(file.readText()).asJsonArray
        return json.map { it.asJsonObject }.map { Exam(it["name"].asString, it["date"].asLong) }
    }

    @UiThread
    private fun onLoad(exams: List<Exam>) {
        TheHolder.examList = exams.sortedBy { it.examDate }
        TheHolder.isConfigLoaded = true
        if(exams.isEmpty()) {
            alert.text = getString(R.string.text_nothing)
            loader.hide(slideOut)
            alert.show(slideIn)
        }else {
            startTimer()
        }
    }
    @UiThread
    private fun startTimer() {
        if(!serviceBound) {
            val intent = Intent(this, TimerService::class.java)
            startService(intent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }else{
            if(!service!!.isTimerActive) {
                service!!.startTimer()
                service!!.registerCallbackListener(this)
            }
        }
    }

    @UiThread
    private fun onError(e: Exception?) {
        e?.printStackTrace()
    }

    @SuppressLint("SetTextI18n")
    override fun callback(type: CallbackType, data:Bundle) {
        runOnUiThread {
            when (type) {
                CallbackType.TICK -> {
                    val d = data.getInt("days")
                    val h = data.getInt("hours")
                    val m = data.getInt("minutes")
                    val s = data.getInt("seconds")
                    val i = data.getInt("exam")
                    val exam = TheHolder.examList[i]
                    days.text = getString(R.string.text_days, String.format("%02d", d), formatWord(d, arrayOf("день", "дня", "дней")))
                    hours.text = getString(R.string.text_hours, String.format("%02d", h), formatWord(h, arrayOf("час", "часа", "часов")))
                    minutes.text = getString(R.string.text_mins, String.format("%02d", m), formatWord(m, arrayOf("минута", "минуты", "минут")))
                    seconds.text = getString(R.string.text_secs, String.format("%02d", s), formatWord(s, arrayOf("секунда", "секунды", "секунд")))
                    count.text = getString(R.string.text_count, i+1)
                    label.text = exam.examName
                    if(hours.visibility != View.VISIBLE ||
                            days.visibility != View.VISIBLE ||
                            minutes.visibility != View.VISIBLE ||
                            seconds.visibility != View.VISIBLE ||
                            count.visibility != View.VISIBLE ||
                            label.visibility != View.VISIBLE ||
                            loader.visibility == View.VISIBLE ||
                            alert.visibility == View.VISIBLE) {
                        loader.hide(slideOut)
                        days.show(slideIn)
                        hours.show(slideIn)
                        minutes.show(slideIn)
                        seconds.show(slideIn)
                        count.show(slideIn)
                        label.show(slideIn)
                        alert.hide(slideOut)
                    }
                }
                CallbackType.START -> {
                    Log.i("callback", "start")
                }
                CallbackType.STOP -> {
                    if(loader.visibility != View.GONE)
                        loader.hide(slideOut)
                    days.hide(slideOut)
                    hours.hide(slideOut)
                    minutes.hide(slideOut)
                    seconds.hide(slideOut)
                    count.hide(slideOut)
                    label.hide(slideOut)
                    if(alert.visibility == View.VISIBLE)
                        alert.hide(slideOut)
                    alert.text = if(data.getString("why").isNotEmpty()) data.getString("why") else getString(R.string.text_stopped)
                    alert.show(slideIn)
                }
                CallbackType.ERROR -> {
                    alert.text = getString(R.string.text_error)
                    alert.show(slideIn)
                }
            }
        }
    }

    private fun formatWord(count: Int, variants: Array<String>): String =
            if((count - count % 10) % 100 != 10) {
                when {
                    count % 10 == 1 -> variants[0]
                    count % 10 in 2..4 -> variants[1]
                    else -> variants[2]
                }
            }else{
                variants[2]
            }

    override fun onDestroy() {
        super.onDestroy()
        if(serviceBound)
            service!!.stopSelf()
        unbindService(serviceConnection)
    }

}