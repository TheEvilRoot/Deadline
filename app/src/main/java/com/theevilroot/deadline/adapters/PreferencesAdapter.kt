package com.theevilroot.deadline.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.theevilroot.deadline.*
import com.theevilroot.deadline.objects.*
import com.theevilroot.deadline.utils.bindView
import com.theevilroot.deadline.utils.hide
import com.theevilroot.deadline.utils.isDateValidForPattern
import java.text.SimpleDateFormat
import java.util.*

class PreferencesAdapter: RecyclerView.Adapter<PreferencesAdapter.PreferenceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.preference_layout, parent, false)
        return PreferenceViewHolder(view, this)
    }
    override fun getItemCount(): Int = TheHolder.userPreferences.count()
    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        holder.bind(TheHolder.userPreferences[position], position)
    }
    class PreferenceViewHolder(itemView: View, private val adapter: PreferencesAdapter): RecyclerView.ViewHolder(itemView) {
        private val title: TextView by bindView(R.id.preference_entry_title)
        private val name: TextView by bindView(R.id.preference_entry_name)
        private val description: TextView by bindView(R.id.preference_entry_description)
        private val switch: Switch by bindView(R.id.preference_entry_switch)
        private val text: TextView by bindView(R.id.preference_entry_text)
        @SuppressLint("SimpleDateFormat")
        fun bind(preference: Preference, position: Int) {
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
                    if(!preference.value.isBoolean)
                        itemView.setOnClickListener { handleEditing(preference, itemView, position) }
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        private fun handleEditing(preference: Preference, itemView: View, position: Int) {
            var builder = MaterialDialog.Builder(itemView.context).title(preference.name).autoDismiss(false)
            with(preference.value) {
                builder = when {
                    isInt ->
                        builder.inputType(InputType.TYPE_CLASS_NUMBER).input("", int.intValue.toString(), {_, _ -> }).onPositive { dialog, action ->
                            val newValue = dialog.inputEditText!!.text.toString()
                            if(newValue.isBlank() || newValue.toIntOrNull() == null) {
                                dialog.setContent("Введите корректное значение")
                                return@onPositive
                            }
                            preference.value = PreferenceInt(newValue.toInt())
                            adapter.notifyItemChanged(position)
                            dialog.dismiss()
                        }
                    isString ->
                            builder.inputType(InputType.TYPE_CLASS_TEXT).input("", string.stringValue, {_, _ -> }).onPositive { dialog, action ->
                                val newValue = dialog.inputEditText!!.text.toString()
                                if(newValue.isBlank()) {
                                    dialog.setContent("Введите корректное значение")
                                    return@onPositive
                                }
                                preference.value = PreferenceString(newValue)
                                adapter.notifyItemChanged(position)
                                dialog.dismiss()
                            }
                    isFloat ->
                            builder.inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL).input("", float.floatValue.toString(), {_, _ -> }).onPositive { dialog, action ->
                                val newValue = dialog.inputEditText!!.text.toString()
                                if(newValue.isBlank() || newValue.toFloatOrNull() == null) {
                                    dialog.setContent("Введите корректное значение")
                                    return@onPositive
                                }
                                preference.value = PreferenceFloat(newValue.toFloat())
                                adapter.notifyItemChanged(position)
                                dialog.dismiss()
                            }
                    isDate -> {
//                        val pattern = preference.value.date.pattern
//                        val oldValue = preference.value.date.dateValue
//                        val sdf = SimpleDateFormat(pattern)
//                        val hint = sdf.format(Date(950008864))
//                        builder.inputType(InputType.TYPE_DATETIME_VARIATION_NORMAL).input(hint, sdf.format(oldValue), {_,_ -> }).onPositive {dialog, action ->
//                            val newValue = dialog.inputEditText!!.text.toString()
//                            if(newValue.isBlank() || !isDateValidForPattern(sdf, newValue)) {
//                                dialog.setContent("Введите корректное значение, например $hint")
//                                return@onPositive
//                            }
//                            preference.value = PreferenceDate(sdf.parse(newValue), pattern)
//                            adapter.notifyItemChanged(position)
//                            dialog.dismiss()
//                        }
                        builder
                    }
                    else -> builder.content("Invalid preference type ${preference.value}")
                }
            }
            builder.positiveText("Ок").negativeText("Отмена").onNegative { dialog, action ->
                dialog.dismiss()
            }.onAny { dialog, which -> adapter.notifyDataSetChanged() }.show()
        }
    }

}