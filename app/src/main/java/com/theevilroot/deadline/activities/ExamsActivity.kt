package com.theevilroot.deadline.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.codekidlabs.storagechooser.StorageChooser
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.theevilroot.deadline.*
import com.theevilroot.deadline.adapters.ExamAdapter
import com.theevilroot.deadline.objects.Exam
import ru.pearx.carbide.Tuple
import ru.pearx.carbide.collections.event.EventList
import ru.pearx.carbide.collections.event.ListEventHandler
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class ExamsActivity: AppCompatActivity() {

    val layout = R.layout.exams_activity

    private val slideIn by load(android.R.anim.slide_in_left)
    private val slideOut by load(android.R.anim.slide_out_right)

    @SuppressLint("SimpleDateFormat")
    val timeFormat = SimpleDateFormat("HH:mm")
    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")

    private val list: RecyclerView by bind(R.id.exams_list)
    private val bottomToolbar: LinearLayout by bind(R.id.exams_bottom_toolbar)
    private val bottomToolbarEdit: ImageButton by bind(R.id.exams_bottom_toolbar_edit)
    private val bottomToolbarDelete: ImageButton by bind(R.id.exams_bottom_toolbar_delete)
    private val bottomToolbarCancel: ImageButton by bind(R.id.exams_bottom_toolbar_cancel)
    private val toolBar: Toolbar by bind(R.id.toolbar)

    private val adapter = ExamAdapter({ exam, pos ->
        handleUnselection(exam, pos)
    }, { exam, pos ->
        handleSelection(exam, pos)
        true
    })

    private fun handleSelection(exam: Exam, pos: Int) {
        if(!buffer[pos].selected) {
            buffer[pos].selected = true
            adapter.notifyItemChanged(pos)
            if(bottomToolbar.visibility != View.VISIBLE)
                bottomToolbar.show(slideIn)
        }
        val single = buffer.filter { it.selected }.size == 1
        bottomToolbarEdit.isEnabled = single
        bottomToolbarEdit.setColorFilter(if(single) ThePreferences.activeColor else ThePreferences.inactiveColor)
    }
    private fun handleUnselection(exam: Exam, pos: Int) {
        if(buffer[pos].selected) {
            buffer[pos].selected = false
            adapter.notifyItemChanged(pos)
            val selectedCount = buffer.filter { it.selected }.size

            if(bottomToolbar.visibility == View.VISIBLE && selectedCount == 0) {
                bottomToolbar.hide(slideOut)
            }
            if(selectedCount < 2) {
                bottomToolbarEdit.isEnabled = true
                bottomToolbarEdit.setColorFilter(ThePreferences.activeColor)
            }
        }
    }

    private var isChanged: Boolean = false
    private val buffer: EventList<Exam> = EventList(ArrayList(), object: ListEventHandler<Exam> {
        override fun onAdd(t: Exam?) { this@ExamsActivity.isChanged = true }
        override fun onAdd(t: MutableCollection<out Exam>?) { this@ExamsActivity.isChanged = true }
        override fun onRemove(o: Exam?, index: Int) { this@ExamsActivity.isChanged = true }
        override fun onRemove(col: MutableCollection<Tuple<Exam, Int>>?) { this@ExamsActivity.isChanged = true }
        override fun onPut(index: Int, t: Exam?) { this@ExamsActivity.isChanged = true }
        override fun onPut(index: Int, t: MutableCollection<out Exam>?) { this@ExamsActivity.isChanged = true }
        override fun onClear(col: MutableCollection<Exam>?) { this@ExamsActivity.isChanged = true }
        override fun onSet(index: Int, prevValue: Exam?, newValue: Exam?) { this@ExamsActivity.isChanged = true }
    })
    
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = ""
        buffer.addAll(TheHolder.examList)
        isChanged = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ThePreferences.backgroundColor
        }
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        adapter.initExams(buffer)
        bottomToolbarEdit.setOnClickListener {
            val exam = buffer.firstOrNull { it.selected } ?: return@setOnClickListener
            showExamEdit("Редактировать экзамен",
                    {name, date, time, cancel, dialog ->
                        name.setText(exam.examName)
                        val dateString = dateFormat.format(exam.examDate)
                        date.setText(dateString.split(" ")[0])
                        time.setText(dateString.split(" ")[1])
                    },
                    {name, _, time, _,_ ->
                        if(name.text.isBlank() || time.text.isBlank()) {
                            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                            return@showExamEdit false
                        }
                        true
                    },
                    { name, date, time, _, _ ->
                        "Изменить имя на '${name.text}', дату и время на ${date.text} ${time.text}?"
                    },
                    { name, date, time, _, dialog,di ->
                        try {
                            val d = dateFormat.parse("${date.text} ${time.text}")
                            buffer[buffer.indexOf(exam)] = Exam(name.text.toString(), d.time)
                            adapter.notifyDataSetChanged()
                            name.text.clear()
                            time.text.clear()
                            dialog.dismiss()
                            bottomToolbar.hide(slideOut)
                        }catch (e: Exception) {
                            e.printStackTrace()
                            di.dismiss()
                        }
                    },
                    {_, _, _, _, dialog, di ->
                        di.dismiss()
                        dialog.dismiss()
                    })
        }
        bottomToolbarDelete.setOnClickListener {
            buffer.removeAll { it.selected }
            adapter.notifyDataSetChanged()
            bottomToolbar.hide(slideOut)
        }
        bottomToolbarCancel.setOnClickListener {
            buffer.forEach { it.selected = false }
            adapter.notifyDataSetChanged()
            bottomToolbar.hide(slideOut)
        }
    }

    private fun showExamEdit(title: String,
                             prepareFunc: (EditText, EditText, EditText, Button, Dialog) -> Unit,
                             saveCondition: (EditText, EditText, EditText, Button, Dialog) -> Boolean,
                             acceptMessage: (EditText, EditText, EditText, Button, Dialog) -> String,
                             onAccept: (EditText, EditText, EditText, Button, Dialog, DialogInterface) -> Unit,
                             onDenied: (EditText, EditText, EditText, Button, Dialog, DialogInterface) -> Unit) {
        val view = layoutInflater.inflate(R.layout.exam_edit_layout, null, false)
        val dialog = AlertDialog.Builder(this).setTitle(title).setView(view).create()
        val name = view.findViewById<EditText>(R.id.exam_edit_name)
        val save = view.findViewById<Button>(R.id.exam_edit_save)
        val cancel = view.findViewById<Button>(R.id.exam_edit_cancel)
        val date = view.findViewById<EditText>(R.id.exam_edit_date)
        val time = view.findViewById<EditText>(R.id.exam_edit_time)
        prepareFunc(name, date, time, cancel, dialog)
        cancel.setOnClickListener {
            name.text.clear()
            dialog.dismiss()
        }
        save.setOnClickListener {
            if(!saveCondition(name, date, time, cancel, dialog))
                return@setOnClickListener
            AlertDialog.Builder(this).
                    setTitle("Подтверждение").
                    setMessage(acceptMessage(name, date, time, cancel, dialog)).
                    setPositiveButton("Да", {di, _ ->
                        onAccept(name, date, time, cancel, dialog, di)
                    }).setNegativeButton("Не-а", {di, _ ->
                onDenied(name, date, time, cancel, dialog, di)
            }).create().show()
        }

        dialog.show()
    }

    private fun showAlert(title: String,
                          message: String = "",
                          icon: Drawable?,
                          positiveText: String = "Ок",
                          positive: (DialogInterface) -> Unit = {_ -> },
                          negativeText: String? = null,
                          negative: (DialogInterface) -> Unit = {_ -> },
                          neutralText: String? = null,
                          neutral: (DialogInterface) -> Unit = {_ -> }): AlertDialog {
        val builder = AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton(positiveText, {di,_ -> positive(di)})
        if(negativeText != null)
            builder.setNegativeButton(negativeText, {di, _ -> negative(di)})
        if(negativeText != null)
            builder.setNeutralButton(neutralText, {di, _ -> neutral(di)})
        if(icon != null)
            builder.setIcon(icon)
        return builder.create()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.exams_toolbar, menu)
        return true
    }

    fun saveExams(file: File,onSuccess: () -> Unit ,onError: (String) -> Unit) {
        thread(true) {
            if(!file.exists()) {
                runOnUiThread {
                    onError("WTF? Config file does not exists?!")
                }
                return@thread
            }
            val json = JsonArray()
            val items = TheHolder.examList.map {
                val obj = JsonObject()
                obj.addProperty("name", it.examName)
                obj.addProperty("date", it.examDate)
                obj
            }
            for (i in items)
                json.add(i)
            file.writeText(GsonBuilder().setPrettyPrinting().create().toJson(json))
            runOnUiThread { onSuccess() }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home ->  {
                if(!isChanged) {
                    this.finish()
                    return true
                }
                showAlert("Отменить изменения?", "", null, "Сохранить и выйти", {di ->
                    TheHolder.examList = buffer.sortedBy { it.examDate }
                    saveExams(File(filesDir, "config"), {
                        Toast.makeText(this, "Файл конфигурации обновлен! Измения сохранены", Toast.LENGTH_SHORT).show()
                        TheHolder.needRefresh = true
                        this.finish()
                    },{msg ->
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    })
                }, "Отменить", {di ->
                    buffer.forEach { it.selected = false }
                    di.dismiss()
                    this.finish()
                }, "Закрыть", {di ->
                    di.dismiss()
                }).show()
            }
            R.id.exams_add -> {
                showExamEdit("Новый экзамен",
                        {_, _, time, _, _ ->
                            time.setText("12:00") },
                        {name, _, time, _,_ ->
                            if(name.text.isBlank() || time.text.isBlank()) {
                                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                                return@showExamEdit false
                            }
                            true
                        },
                        {name, date, time, _, _ ->
                            "Создать экзамен '${name.text}' ${date.text} в ${time.text}?"
                        },
                        {name, date, time, _, dialog,di ->
                            try {
                                val d = dateFormat.parse("${date.text} ${time.text}")
                                buffer.add(Exam(name.text.toString(), d.time))
                                adapter.notifyItemInserted(adapter.itemCount - 1)
                                name.text.clear()
                                time.text.clear()
                                dialog.dismiss()
                            }catch (e: Exception) {
                                e.printStackTrace()
                                di.dismiss()
                            }
                        },
                        {_, _, _, _, dialog, di ->
                            di.dismiss()
                            dialog.dismiss()
                        })
            }
            R.id.exams_save -> {
                TheHolder.examList = buffer.sortedBy { it.examDate }
                saveExams(File(filesDir, "config"), {
                    Toast.makeText(this, "Файл конфигурации обновлен! Измения сохранены", Toast.LENGTH_SHORT).show()
                    TheHolder.needRefresh = true
                    this.finish()
                },{msg ->
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                })
            }

            R.id.exams_export -> {
                if(buffer.isEmpty()) {
                    showAlert("Нельзя экспортировать пустой список экзаменов", icon = resources.getDrawable(R.drawable.alert_circle_outline).apply { this.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN) }, positiveText = "Окей").show()
                    return true
                }else{
                    val selected = buffer.filter { it.selected }
                    val toExport = if(selected.isNotEmpty()) { selected }else{ buffer }
                    AlertDialog.Builder(this).setTitle("Экспортировать экзамены").setMessage("Вы действительно хотите экспортировать данные экзамены: \n${toExport.map { it.examName }.reduce { s1, s2 -> "$s1\n$s2" }}").setPositiveButton("Да", {di, _ ->
                        val json = JsonArray()
                        toExport.forEach { json.add(JsonObject().apply { this.addProperty("name", it.examName); this.addProperty("date", it.examDate) }) }
                        val chooser = StorageChooser.Builder().
                                withActivity(this).
                                withFragmentManager(fragmentManager).
                                withMemoryBar(true).
                                allowCustomPath(true).
                                setType(StorageChooser.DIRECTORY_CHOOSER).
                                disableMultiSelect().build()
                        chooser.setOnSelectListener {
                            val dir = File(it)
                            if(!dir.exists() || !dir.isDirectory) {
                                Toast.makeText(this, "Invalid directory!",Toast.LENGTH_LONG).show()
                                return@setOnSelectListener
                            }
                            val file = File(dir, "exams_dump_${timeFormat.format(Date())}.json")
                            if(!file.createNewFile()) {
                                Toast.makeText(this, "Unable to create new file on ${file.absolutePath}", Toast.LENGTH_LONG).show()
                                return@setOnSelectListener
                            }
                            file.writeText(GsonBuilder().setPrettyPrinting().create().toJson(json))
                            di.dismiss()
                            Toast.makeText(this, "Экзамены экспортированы в ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                        chooser.show()
                    }).setNegativeButton("Нет!", {di,_ -> di.dismiss()}).setNeutralButton("Что это значит?", {di, _ ->
                        Toast.makeText(this, "Guide", Toast.LENGTH_LONG).show()
                    }).create().show()
                }
            }
            R.id.exams_import -> {
                val chooser = StorageChooser.Builder().
                        withActivity(this).
                        withFragmentManager(fragmentManager).
                        withMemoryBar(true).
                        allowCustomPath(true).
                        setType(StorageChooser.FILE_PICKER).
                        disableMultiSelect().build()
                chooser.setOnSelectListener {
                    val file = File(it)
                    if(!file.exists() || !file.isFile) {
                        Toast.makeText(this, "Invalid file!",Toast.LENGTH_LONG).show()
                        return@setOnSelectListener
                    }
                    val json = JsonParser().parse(file.readText()).asJsonArray
                    val newExams = json.map { it.asJsonObject }.map { Exam(it["name"].asString, it["date"].asLong) }
                    if(buffer.isNotEmpty()) {
                        showAlert(title = "Импорт экзаменов",
                                message = "Заменить нынешний список экзаменов новыми или слить их в один?",
                                icon = resources.getDrawable(R.drawable.ic_import),
                                positiveText = "Соеденить в один", positive = { di ->
                            newExams.forEach {
                                if (!buffer.isContains(it))
                                    buffer.add(it)
                                else
                                    Toast.makeText(this, "Экзамен ${it.examName} уже добален", Toast.LENGTH_SHORT).show()
                            }
                            adapter.notifyDataSetChanged()
                        }, neutralText = "Заменить новыми", neutral = { di ->
                            buffer.clear()
                            buffer.addAll(newExams)
                            adapter.notifyDataSetChanged()
                        }, negativeText = "Отмена", negative = { di ->
                            di.dismiss()
                        }).show()
                    }else{
                        buffer.addAll(newExams)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "Экзамены импортированы!", Toast.LENGTH_LONG).show()
                    }
                }
                chooser.show()
            }
         }
        return super.onOptionsItemSelected(item)
    }
}