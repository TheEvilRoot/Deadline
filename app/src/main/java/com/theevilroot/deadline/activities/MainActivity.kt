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
import android.widget.Toast
import com.google.gson.*
import com.nipunbirla.boxloader.BoxLoaderView
import com.theevilroot.deadline.*
import com.theevilroot.deadline.objects.*
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
            window.statusBarColor = Colors.secondBackgroundColor()
        }
        settings.setColorFilter(Colors.iconsColor())
        examSettings.setColorFilter(Colors.iconsColor())
        examSettings.setOnClickListener {
            if(!TheHolder.isConfigLoaded)
                return@setOnClickListener
            val intent = Intent(this, ExamsActivity::class.java)
            startActivity(intent)
        }
        settings.setOnClickListener {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
        }
        if(!TheHolder.isConfigLoaded) {
            load()
        } else {
            startTimer()
        }
    }
    private fun load() {
        loader.setSpeed(10)
        thread(true) {
            try{
                val config = File(filesDir, "config")
                val prefs = File(filesDir, "preferences")
                val exams = if(config.exists()) parseConfig(config) else writeAndGetConfig(config)
                val preferences = if(prefs.exists()) parsePreferences(prefs) else writeAndGetPreferences(prefs)
                runOnUiThread { onLoad(exams, preferences) }
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
    private fun writeAndGetConfig(file: File): List<Exam> {
        file.createNewFile()
        file.writeText("[]")
        return parseConfig(file)
    }

    private fun handlePreferenceFile(json: JsonElement): JsonObject? {
        if(json.isJsonArray)
            return assimilatePreferences(json.asJsonArray)
        if(!json.isJsonObject)
            return null
        val obj = json.asJsonObject
        return if(!obj.has("version")) {
            if(obj.has("preferences") && obj["preferences"].isJsonArray)
                assimilatePreferences(obj["preferences"].asJsonArray)
            else null
        }else {
            if(obj["version"].asInt != TheHolder.preferenceVersion)
                assimilatePreferences(obj["preferences"].asJsonArray)
            else obj
        }
    }

    private fun assimilatePreferences(array: JsonArray): JsonObject {
        val obj = JsonObject().apply { addProperty("version", TheHolder.preferenceVersion) }
        val preferences = JsonArray()
        val jsonPreferences = array.map { it.asJsonObject }.map {
            if(it["isGroup"].asBoolean)
                Preference(true, it["title"].asString)
            else
                Preference(false, name = it["name"].asString, description = it["description"].asString, type = when(it["type"].asString.toLowerCase()) {
                    "int" -> PT_INT
                    "string" -> PT_STRING
                    "time" -> PT_TIME
                    "date" -> PT_DATE
                    "boolean" -> PT_BOOLEAN
                    else -> PT_STRING
                }, value = it["value"].asString, id = it["id"].asString)
        }
        TheHolder.userPreferences.forEach { user ->
            val preference = jsonPreferences.filter { json -> user.id == json.id }.getOrNull(0)
            if(preference != null) {
                preferences.add(JsonObject().apply {
                    addProperty("isGroup", user.isGroup)
                    if(user.isGroup) {
                        addProperty("title", user.title)
                    }else {
                        addProperty("name", user.name)
                        addProperty("description", user.description)
                        addProperty("type", when (user.type) {
                            PT_STRING -> "string"
                            PT_BOOLEAN -> "boolean"
                            PT_DATE -> "date"
                            PT_TIME -> "time"
                            PT_INT -> "int"
                            else -> "string"
                        })
                        addProperty("value", preference.value)
                        addProperty("id", user.id)
                    }
                })
            }else{
                preferences.add(user.toJsonObject())
            }
        }
        obj.add("preferences", preferences)
        return obj
    }

    private fun parsePreferences(file: File): List<Preference> {
        val json = handlePreferenceFile(JsonParser().parse(file.readText())) ?: return writeAndGetPreferences(file)
        return json["preferences"].asJsonArray.map { it.asJsonObject }.map {
            if(it["isGroup"].asBoolean)
                Preference(true, it["title"].asString)
            else
                Preference(false, name = it["name"].asString, description = it["description"].asString, type = when(it["type"].asString.toLowerCase()) {
                    "int" -> PT_INT
                    "string" -> PT_STRING
                    "time" -> PT_TIME
                    "date" -> PT_DATE
                    "boolean" -> PT_BOOLEAN
                    else -> PT_STRING
                }, value = it["value"].asString, id = it["id"].asString)
        }
    }
    private fun writeAndGetPreferences(file: File): List<Preference> {
        file.createNewFile()
        writePreferences(file)
        return TheHolder.userPreferences
    }
    private fun parseConfig(file: File): List<Exam> {
        val json = JsonParser().parse(file.readText()).asJsonArray
        return json.map { it.asJsonObject }.map { Exam(it["name"].asString, it["date"].asLong) }
    }
    @UiThread
    private fun onLoad(exams: List<Exam>, preferences: List<Preference>) {
        TheHolder.examList = exams.sortedBy { it.examDate }
        TheHolder.userPreferences = preferences
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
    override fun onDestroy() {
        super.onDestroy()
        if(serviceBound)
            service!!.stopSelf()
        unbindService(serviceConnection)
    }

}