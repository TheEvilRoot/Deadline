package com.theevilroot.deadline.activities

import android.content.Context
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
import com.theevilroot.deadline.utils.bind
import java.io.File


class PreferencesActivity: AppCompatActivity() {
    val layout = R.layout.preferences_activity

    private val toolBar: Toolbar by bind(R.id.toolbar)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when(item.itemId) {
                    android.R.id.home -> { finish(); true }
                    else -> false
            }

    class PreferencesFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.app_preferences)
        }
    }
}