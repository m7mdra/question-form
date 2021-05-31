package com.m7mdra.questionForm

import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

abstract class TextInputEditTextWatcher(private val editText: TextInputEditText) : TextWatcher {
    fun removeWatcher() {
        editText.removeTextChangedListener(this)
    }

}

