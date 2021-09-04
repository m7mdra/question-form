package com.m7mdra.questionForm.question

import android.text.InputType

class InputQuestion(
    title: String,
    val id:String,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    hint: String = "",
    val mandatory: Boolean = false

) :
    Question<String?>(title, QuestionType.Input,id = id,required = mandatory) {
    override var hasError: Boolean = false

    override var value: String? = null
    override fun validate(): Boolean {
        return value != null && value!!.isNotBlank() && value!!.isNotEmpty()
    }

    override fun collect(): Pair<String,String?> {
        return id to value
    }

    override fun update(value: String?) {

        this.value = value
    }

}
