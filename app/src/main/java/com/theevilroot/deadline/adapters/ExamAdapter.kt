package com.theevilroot.deadline.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.theevilroot.deadline.R
import com.theevilroot.deadline.Colors
import com.theevilroot.deadline.bindView
import com.theevilroot.deadline.objects.Exam
import java.text.SimpleDateFormat

class ExamAdapter(val onClick: (Exam, Int) -> Unit, val onLongClick: (Exam, Int) -> Boolean): RecyclerView.Adapter<ExamAdapter.ExamHolder>() {
     var exams: List<Exam> = emptyList()
    fun initExams(ex: List<Exam>) {
        this.exams = ex
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.exam_layout, parent, false)
        return ExamHolder(view)
    }
    override fun getItemCount(): Int = exams.count()
    override fun onBindViewHolder(holder: ExamHolder, position: Int) {
        holder.bind(exams[position], onClick,onLongClick ,position)
    }
    class ExamHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SimpleDateFormat")
        private val format = SimpleDateFormat("HH:mm dd.MM.yyyy")
        private val name: TextView by bindView(R.id.exam_name)
        private val date: TextView by bindView(R.id.exam_date)
        private val state: TextView by bindView(R.id.exam_state)
        fun bind(exam: Exam, onClick: (Exam, Int) -> Unit, onLongClick: (Exam, Int) -> Boolean, position: Int) {
            name.text = exam.examName
            date.text = format.format(exam.examDate)
            if(exam.state()) {
                state.text = "Еще будет!"
                state.setTextColor(Color.RED)
            }else{
                state.text = "Прошёл"
                state.setTextColor(Color.GREEN)
            }
            itemView.setOnClickListener { onClick(exam, position) }
            itemView.setOnLongClickListener { onLongClick(exam, position) }
            when {
                exam.selected -> itemView.setBackgroundColor(Colors.selectionColor())
                (position+1) % 2 == 0 -> itemView.setBackgroundColor(Colors.backgroundColor())
                else -> itemView.setBackgroundColor(Colors.secondBackgroundColor())
            }
        }
    }

}