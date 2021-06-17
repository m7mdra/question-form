package com.m7mdra.questionForm.question

import android.text.InputType

class InputQuestion(
    title: String,

    inputType: Int = InputType.TYPE_CLASS_TEXT,
    hint: String = ""

) :
    Question<String?>(title, QuestionType.Input) {
    override var hasError: Boolean = false

    override var value: String? = null
    override fun validate(): Boolean {
        return value != null && value!!.isNotBlank() && value!!.isNotEmpty()
    }

    override fun collect(): String? {
        return value
    }

    override fun update(value: String?) {

        this.value = value
    }

}
