package com.m7mdra.questionForm.question

import java.io.File

class AudioQuestion(title: String, val id: String) :
    Question<File?>(title, questionType = QuestionType.Audio, id = id) {
    override var value: File? = null

    override var hasError: Boolean = false


    override fun validate(): Boolean {

        val b = value != null
        hasError = b
        return b
    }

    override fun collect(): Pair<String, File?> {
        return id to value
    }

    override fun update(value: File?) {
        this.value = value
        validate()
    }

}