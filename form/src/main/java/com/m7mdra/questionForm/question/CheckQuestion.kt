package com.m7mdra.questionForm.question

class CheckQuestion(
    title: String,
    val entries: List<String>,
    val id: String,
    private val mandatory: Boolean = false
) :
    Question<List<String>>(title, QuestionType.Check, id = id, required = mandatory) {
    override var hasError: Boolean = false

    override var value: List<String> = listOf()
    var selectionMap = mutableMapOf<Int, String>()

    override fun validate(): Boolean {
        hasError = selectionMap.isEmpty() && mandatory
        return selectionMap.isNotEmpty()
    }

    override fun collect(): Pair<String, List<String>> {
        return id to selectionMap.values.toList()
    }

    override fun update(value: List<String>) {
        this.value = value
    }
}
