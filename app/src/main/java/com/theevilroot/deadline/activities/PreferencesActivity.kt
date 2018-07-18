package com.theevilroot.deadline.activities

import android.os.Build
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.FrameLayout
import com.theevilroot.deadline.*
import com.theevilroot.deadline.adapters.PreferencesAdapter
import com.theevilroot.deadline.utils.bind
import com.theevilroot.deadline.utils.writePreferences
import java.io.File

/**
 * Work In Progress!
 *
 * NOT RELEASE FEATURE!
 */

class PreferencesActivity: AppCompatActivity() {
    val layout = R.layout.preferences_activity

    private val toolBar: Toolbar by bind(R.id.toolbar)
    private val container: FrameLayout by bind(R.id.preferences_container)

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
        fragmentManager.beginTransaction().add(R.id.preferences_container, PreferencesFragment()).commit()
    }
    class PreferencesFragment: PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.app_preferences)
        }
    }

}