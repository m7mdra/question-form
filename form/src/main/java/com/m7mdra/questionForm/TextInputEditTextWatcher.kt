package com.m7mdra.questionForm

import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

abstract class TextInputEditTextWatcher(private val editText: TextInputEditText) : TextWatcher {
    fun removeWatcher() {
        editText.removeTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //dose nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //dose nothing
    }

}

