package com.m7mdra.questionForm.question

import java.io.File

class AudioQuestion(
    title: String,
    val id: String,
    private val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf()
) :
    Question<File?>(
        title,
        questionType = QuestionType.Audio,
        id = id,
        required = mandatory,
        extraParams = params
    ) {
    override var value: File? = null

    override var hasError: Boolean = false

    override fun validate(): Boolean {

        val valid = isValid()
        hasError = !valid
        return valid
    }

    override fun isValid(): Boolean {
        return if (required) {
            value != null
        } else {
            true
        }
    }



    override fun update(value: File?) {
        this.value = value
        validate()
    }

}
