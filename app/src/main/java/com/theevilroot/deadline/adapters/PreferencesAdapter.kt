package com.theevilroot.deadline.adapters

import android.content.res.ColorStateList
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.theevilroot.deadline.R
import com.theevilroot.deadline.bindView
import com.theevilroot.deadline.objects.ColorPreference

/**
 * Work In Progress!
 *
 * NOT RELEASE FEATURE!
 */

class PreferencesAdapter: RecyclerView.Adapter<PreferencesAdapter.PreferenceViewHolder>() {

    private var preferences: List<ColorPreference> = emptyList()

    fun addPreferences(list: List<ColorPreference>) {
        preferences = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.color_preference_layout, parent, false)
        return PreferenceViewHolder(view)
    }

    override fun getItemCount(): Int = preferences.count()

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        holder.bind(preferences[position])
    }

    class PreferenceViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        private val title: TextView by bindView(R.id.preference_title)
        private val subtitle: TextView by bindView(R.id.preference_subtitle)
        private val color: FloatingActionButton by bindView(R.id.preference_color)
        fun bind(preference: ColorPreference) {
            title.text = preference.title
            subtitle.text = preference.subtitle
            color.backgroundTintList = ColorStateList.valueOf(preference.value)
        }
    }

}