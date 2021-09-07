package com.m7mdra.questionForm.question

class DropdownQuestion(
    title: String,
    val entries: List<String>,
    val id: String,
    private val mandatory: Boolean = false
) :
    Question<String?>(title, QuestionType.Dropdown, id = id, required = mandatory) {
    override var hasError: Boolean = false
    override var value: String? = null

    override fun validate(): Boolean {
        val valid = isValid()
        hasError = !valid
        return valid
    }

    private fun isValid(): Boolean {
        return if (required) {
            value != null
        } else {
            true
        }
    }


    override fun collect(): Pair<String, String?> {
        return id to value
    }

    override fun update(value: String?) {
        this.value = value
    }
}
