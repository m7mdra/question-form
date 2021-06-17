package com.m7mdra.questionForm.question

import java.io.File

class AudioQuestion(title: String) : Question<File?>(title, questionType = QuestionType.Audio) {
    override var value: File? = null

    override var hasError: Boolean = false


    override fun validate(): Boolean {

        val b = value != null
        hasError = b
        return b
    }

    override fun collect(): File? {
        return value
    }

    override fun update(value: File?) {
        this.value = value
        validate()
    }

}
