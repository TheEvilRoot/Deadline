package com.theevilroot.deadline.utils

import android.text.Editable
import android.text.TextWatcher

class TextWatcherWrapper(private val afterTextChangedEvent: (Editable) -> Unit = {_ -> },
                         private val onTextChangedEvent: (CharSequence, Int, Int, Int) -> Unit = {_,_,_,_ -> },
                         private val beforeTextChangedEvent: (CharSequence, Int, Int, Int) -> Unit = {_,_,_,_ -> }):TextWatcher  {
    override fun afterTextChanged(s: Editable) {
        afterTextChangedEvent(s)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        beforeTextChangedEvent(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        onTextChangedEvent(s, start, before, count)
    }

}