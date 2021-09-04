package com.m7mdra.questionForm.question

class ImageQuestion(
    title: String,
    private val maxInput: Int = 1,
    private val minInput: Int = 1,
    val id: String,
    val mandatory: Boolean = false
) :
    Question<MutableList<String>>(title, QuestionType.Image, id = id, required = mandatory) {
    override var hasError: Boolean = false
    override var value: MutableList<String> = mutableListOf()


    override fun validate(): Boolean {
        hasError = value.isEmpty() && mandatory


        return value.isNotEmpty()
    }

    override fun collect(): Pair<String, MutableList<String>> {
        return id to value
    }

    override fun update(value: MutableList<String>) {
        this.value.addAll(value)
    }


}