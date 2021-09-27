package com.m7mdra.questionForm.question

import java.io.File

class VideoQuestion(
    title: String,  id: String,
    val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf()

) : Question<File?>(
    title,
    questionType = QuestionType.Video,
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

    override fun isValid() = if (required) {
        value != null
    } else {
        true
    }


    override fun update(value: File?) {
        this.value = value
    }

    override fun toString(): String {
        return "VideoQuestion(title = '$title', id='$id', mandatory=$mandatory, value=$value, hasError=$hasError)"
    }

}
