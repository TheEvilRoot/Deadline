package com.theevilroot.deadline.activities

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.theevilroot.deadline.*
import com.theevilroot.deadline.adapters.PreferencesAdapter
import java.io.File

/**
 * Work In Progress!
 *
 * NOT RELEASE FEATURE!
 */

class PreferencesActivity: AppCompatActivity() {
    val layout = R.layout.preferences_activity
    private val toolBar: Toolbar by bind(R.id.toolbar)
    private val preferenceList: RecyclerView by bind(R.id.preferences_list)
    lateinit var adapter: PreferencesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Colors.backgroundColor()
        }
        supportActionBar!!.title = "Настройки"
        preferenceList.layoutManager = LinearLayoutManager(this)
        adapter = PreferencesAdapter()
        preferenceList.adapter = adapter
    }
    override fun onDestroy() {
        super.onDestroy()
        savePreferences()
    }
    private fun savePreferences() {
        val file = File(filesDir, "preferences")
        if(!file.exists()) // WTF???
            file.createNewFile()
        writePreferences(file)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                this.finish()
            }
        }
        return true
    }
}