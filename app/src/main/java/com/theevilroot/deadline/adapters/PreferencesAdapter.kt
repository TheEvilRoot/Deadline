package com.theevilroot.deadline.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.theevilroot.deadline.*
import com.theevilroot.deadline.objects.*
import com.theevilroot.deadline.utils.bindView
import com.theevilroot.deadline.utils.hide
import java.text.SimpleDateFormat

class PreferencesAdapter: RecyclerView.Adapter<PreferencesAdapter.PreferenceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.preference_layout, parent, false)
        return PreferenceViewHolder(view)
    }
    override fun getItemCount(): Int = TheHolder.userPreferences.count()
    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        holder.bind(TheHolder.userPreferences[position])
    }
    class PreferenceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val title: TextView by bindView(R.id.preference_entry_title)
        private val name: TextView by bindView(R.id.preference_entry_name)
        private val description: TextView by bindView(R.id.preference_entry_description)
        private val switch: Switch by bindView(R.id.preference_entry_switch)
        private val text: TextView by bindView(R.id.preference_entry_text)
        @SuppressLint("SimpleDateFormat")
        fun bind(preference: Preference) {
            if(preference.isGroup) {
                name.hide()
                description.hide()
                switch.hide()
                text.hide()
                title.text = preference.title
                itemView.setBackgroundColor(Colors.backgroundColor())
            }else{
                title.hide()
                name.text = preference.name
                description.text = preference.description
                switch.setOnCheckedChangeListener { _, isChecked ->
                    preference.value = PreferenceBoolean(isChecked)
                }
                with(preference.value) {
                    when {
                        isBoolean -> {
                            val value = boolean.booleanValue
                            text.hide()
                            switch.isChecked = value
                        }
                        isString -> {
                            val value = string.stringValue
                            switch.hide()
                            text.text = value
                        }
                        isInt-> {
                            val value = int.intValue
                            switch.hide()
                            text.text = value.toString()
                        }
                        isFloat -> {
                            val value = float.floatValue
                            switch.hide()
                            text.text = value.toString()
                        }
                        isDate -> {
                            val datePreference = date
                            switch.hide()
                            text.text = SimpleDateFormat(datePreference.pattern).format(datePreference.dateValue)
                        }
                    }
                }
            }
        }
    }

}