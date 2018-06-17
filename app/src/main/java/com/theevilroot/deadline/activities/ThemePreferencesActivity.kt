package com.theevilroot.deadline.activities

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.theevilroot.deadline.R
import com.theevilroot.deadline.ThePreferences
import com.theevilroot.deadline.adapters.PreferencesAdapter
import com.theevilroot.deadline.bind
import com.theevilroot.deadline.objects.ColorPreference

/**
 * Work In Progress!
 *
 * NOT RELEASE FEATURE!
 */

class ThemePreferencesActivity: AppCompatActivity() {

    val layout = R.layout.theme_preferences_activity

    private val toolBar: Toolbar by bind(R.id.toolbar)
    private val themeList: RecyclerView by bind(R.id.theme_list)

    private lateinit var colorsAdapter: PreferencesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ThePreferences.backgroundColor
        }
        colorsAdapter = PreferencesAdapter()
        colorsAdapter.addPreferences(listOf(
                ColorPreference("Цвет фона", "Основной фон на экране настроек!", ThePreferences.backgroundColor),
                ColorPreference("Еще цвет фона", "Второй цвет для фона! Используется на главном экране и в листах!", ThePreferences.secondBackgroundColor),
                ColorPreference("Цвет иконок", "Цвет для всех иконок. НЕ растространяется на кнопки!", ThePreferences.iconsColor),
                ColorPreference("Цвет текста", "Цвет для всего текста!", ThePreferences.textColor),
                ColorPreference("Цвет выделения", "Цвет для выделения в листах!", ThePreferences.selectionColor),
                ColorPreference("Цвет активных кнопок", "Цвет для иконок на активных кнопках", ThePreferences.activeColor),
                ColorPreference("Цвет не активных кнопок", "Цвет для иконок на не активных кнопках", ThePreferences.inactiveColor)
        ))
        themeList.layoutManager = LinearLayoutManager(this)
        themeList.adapter = colorsAdapter
    }

}