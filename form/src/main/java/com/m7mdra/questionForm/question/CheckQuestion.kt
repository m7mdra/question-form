package com.m7mdra.questionForm.question

class CheckQuestion(title: String, val entries: List<String>) :
    Question<List<String>>(title, QuestionType.Check) {
    override var hasError: Boolean = false

    override var value: List<String> = listOf()
    var selectionMap = mutableMapOf<Int, String>()
    override fun validate(): Boolean {
        return value.isNotEmpty()
    }

    override fun collect(): List<String> {
        return selectionMap.values.toList()
    }

    override fun update(value: List<String>) {
        this.value = value
    }
}
