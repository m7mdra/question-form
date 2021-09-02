package com.m7mdra.questionForm.question

class ImageQuestion(
    title: String,
    private val maxInput: Int,
    private val minInput: Int,
    val id: String
) :
    Question<MutableList<String>>(title, QuestionType.Image, id = id) {
    override var hasError: Boolean = false
    override var value: MutableList<String> = mutableListOf()


    override fun validate(): Boolean {

        val size = value.size
        return size in (minInput + 1) until maxInput
    }

    override fun collect(): Pair<String, MutableList<String>> {
        return id to value
    }

    override fun update(value: MutableList<String>) {
        this.value.addAll(value)
    }


}