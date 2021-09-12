package com.m7mdra.questionForm.question

import com.m7mdra.questionForm.log

class ImageQuestion(
    title: String,
    private val maxInput: Int = 1,
    private val minInput: Int = 1,
    val id: String,
    val mandatory: Boolean = false
) :
    Question<MutableList<String>>(title, QuestionType.Image, id = id, required = mandatory) {
    override var value: MutableList<String> = mutableListOf()

    override var hasError: Boolean = false

    override fun validate(): Boolean {
        val valid = isValid()
        hasError = !valid
        return valid

    }

    private fun isValid(): Boolean {
        return if (required) {
            value.isNotEmpty()
        } else {
            true
        }
    }

    override fun collect(): Pair<String, MutableList<String>> {
        return id to value
    }

    override fun update(value: MutableList<String>) {
        this.value.addAll(value)
        "UPDATED LIST WITH VALUE: $value ${isValid()} error: $hasError".log()
    }


}