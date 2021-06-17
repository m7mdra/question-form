package com.m7mdra.questionForm.question

import java.io.File

class AudioQuestion(title: String) : Question<File?>(title, questionType = QuestionType.Audio) {
    override var value: File? = null

    override var hasError: Boolean = false
        get() {
            return !validate()
        }


    override fun validate(): Boolean {

        return value != null
    }

    override fun collect(): File? {
        return value
    }

    override fun update(value: File?) {
        this.value = value
    }

}
