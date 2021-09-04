package com.m7mdra.questionForm.question

class DropdownQuestion(title: String, val entries: List<String>, val id: String,val mandatory: Boolean = false) :
    Question<String>(title, QuestionType.Dropdown, id = id,required = mandatory) {
    override var hasError: Boolean = false

    override var value: String = ""
    override fun validate(): Boolean {
        return value.isNotBlank() && value.isNotEmpty()
    }

    override fun collect(): Pair<String, String> {
        return id to value
    }

    override fun update(value: String) {
        this.value = value
    }
}
