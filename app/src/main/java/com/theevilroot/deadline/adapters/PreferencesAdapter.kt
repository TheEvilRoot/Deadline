package com.theevilroot.deadline.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import com.theevilroot.deadline.*
import com.theevilroot.deadline.objects.*

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
        private val editField: EditText by bindView(R.id.preference_edit_field)
        private val editSave: Button by bindView(R.id.preference_edit_save)
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
                switch.setOnCheckedChangeListener { buttonView, isChecked ->
                    preference.value = isChecked.toString()
                }
                when (preference.type) {
                    PT_BOOLEAN -> {
                        val value = preference.type.parse(preference.value)
                        text.hide()
                        switch.isChecked = value!!
                    }
                    PT_STRING -> {
                        val value = preference.type.parse(preference.value)
                        switch.hide()
                        text.text = value
                    }
                    PT_INT -> {
                        val value = preference.type.parse(preference.value)
                        switch.hide()
                        text.text = value.toString()
                    }
                    PT_TIME -> {
                        switch.hide()
                        text.text = preference.value
                    }
                    PT_DATE -> {
                        switch.hide()
                        text.text = preference.value
                    }
                }
                editField.addTextChangedListener(TextWatcherWrapper(onTextChangedEvent = {s, start, before,count ->
                    if(editField.text.toString() == preference.value)
                        editSave.text = "Отмена"
                    else
                        editSave.text = "Сохранить"
                }))
                editSave.setOnClickListener {
                    if(preference.value == editField.text.toString()) {
                        editField.hide()
                        editSave.hide()
                        text.show()
                        return@setOnClickListener
                    }
                    if(preference.type.parse(editField.text.toString()) == null) {
                        editField.setTextColor(Color.RED)
                        return@setOnClickListener
                    }
                    preference.value = editField.text.toString()
                    text.text = editField.text.toString()
                    editField.hide()
                    editSave.hide()
                    text.show()
                }
                if (preference.type != PT_BOOLEAN) {
                    itemView.setOnClickListener {
                        if(editField.visibility == View.VISIBLE || editSave.visibility == View.VISIBLE) {
                            text.show()
                            editField.hide()
                            editSave.hide()
                        }else{
                            text.hide()
                            editField.show()
                            editSave.show()
                            editField.setText(preference.value)
                        }
                    }
                }
            }
        }
    }

}