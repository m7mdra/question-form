package com.m7mdra.questionForm.question

import java.io.File

class VideoQuestion(title: String) : Question<File?>(title, questionType = QuestionType.Video) {
    override var value: File? = null
    override var hasError: Boolean = false

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
