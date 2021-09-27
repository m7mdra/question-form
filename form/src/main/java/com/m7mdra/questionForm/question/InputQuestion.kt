package com.m7mdra.questionForm.question

import android.text.InputType

class InputQuestion(
    title: String,
    id: String,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    hint: String = "",
    val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf()


) :
    Question<String?>(
        title,
        QuestionType.Input,
        id = id,
        required = mandatory,
        extraParams = params
    ) {
    override var hasError: Boolean = false

    override var value: String? = null
    override fun validate(): Boolean {

        val valid = isValid()
        hasError = !valid
        return valid

    }

    override fun isValid() = if (required) {
        value != null
    } else {
        true
    }


    override fun update(value: String?) {

        this.value = value
    }

}
