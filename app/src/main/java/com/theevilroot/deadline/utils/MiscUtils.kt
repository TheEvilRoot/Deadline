package com.theevilroot.deadline.utils
import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.AnimRes
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
val timeFormat = SimpleDateFormat("HH:mm")
@SuppressLint("SimpleDateFormat")
val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")


fun formatWord(count: Int, variants: Array<String>): String = if((count - count % 10) % 100 != 10) {
    when {
        count % 10 == 1 -> variants[0]
        count % 10 in 2..4 -> variants[1]
        else -> variants[2]
    }
}else{ variants[2] }
/**
 * Bind View from activity with 'by' operator
 *
 * Example: val textView: TextView by bind(R.id.textView)
 */
fun <T : View> AppCompatActivity.bind(@IdRes res: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazy { findViewById<T>(res) }
}

/**
 * Bind View from RecyclerView's ViewHolder
 *
 * Example: val textView: TextView by bindView(R.id.textView)
 */
fun <T : View> RecyclerView.ViewHolder.bindView(@IdRes res: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazy { itemView.findViewById<T>(res) }
}

/**
 * Load animation with 'by' operator
 *
 * Example: val animation by load(R.anim.anim_name)
 */
fun Context.load(@AnimRes res: Int): Lazy<Animation> {
    @Suppress("UNCHECKED_CAST")
    return lazy { AnimationUtils.loadAnimation(this, res) }
}

/**
 * Implementation of default visibility property of View
 */
fun View.show() {
    this.visibility = View.VISIBLE
}

/**
 * Implementation of default visibility property of View
 * Now with Animation =)
 */
fun View.show(animation: Animation) {
    if(this.visibility != View.VISIBLE) {
        this.startAnimation(animation)
        this.visibility = View.VISIBLE
    }
}
/**
 * Implementation of default visibility property of View
 */
fun View.hide() {
    this.visibility = View.GONE
}
/**
 * Implementation of default visibility property of View
 * Now with Animation =)
 */
fun View.hide(animation: Animation) {
    if(this.visibility != View.GONE) {
        this.startAnimation(animation)
        this.visibility = View.GONE
    }
}

/**
 * Implementation of default text property of TextView
 */
fun TextView.clear() {
    this.text = ""
}

fun <E> List<E>.isContains(e: E): Boolean {
    for(i in this)
        if(i == e)
            return true
    return false
}