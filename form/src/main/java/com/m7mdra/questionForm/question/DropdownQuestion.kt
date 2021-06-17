package com.m7mdra.questionForm.question

class DropdownQuestion(title: String, val entries: List<String>) :
    Question<String>(title, QuestionType.Dropdown) {
    override var hasError: Boolean = false

    override var value: String = ""
    override fun validate(): Boolean {
        return value.isNotBlank() && value.isNotEmpty()
    }

    override fun collect(): String {
        return value
    }

    override fun update(value: String) {
        this.value = value
    }
}
