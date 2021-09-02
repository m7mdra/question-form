package com.m7mdra.questionForm.question

import java.io.File

class VideoQuestion(
    title: String, val id: String
) : Question<File?>(title, questionType = QuestionType.Video, id = id) {
    override var value: File? = null
    override var hasError: Boolean = false

    override fun validate(): Boolean {
        return value != null
    }

    override fun collect(): Pair<String, File?> {
        return id to value
    }

    override fun update(value: File?) {
        this.value = value
    }
}
