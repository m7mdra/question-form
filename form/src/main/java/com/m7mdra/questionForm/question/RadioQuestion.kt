package com.m7mdra.questionForm.question

class RadioQuestion(
    title: String, val entries: List<String>, val id: String,
    val mandatory: Boolean
) :
    Question<String?>(title, QuestionType.Radio, id = id, required = mandatory) {
    override var value: String? = null
    override var hasError: Boolean = false

    override fun validate(): Boolean {
        val valid = isValid()
        hasError = !valid
        return valid

    }

    private fun isValid() = if (required) {
        value != null
    } else {
        true
    }

    override fun collect(): Pair<String, String?> {
        return id to value
    }

    override fun update(value: String?) {
        this.value = value

    }

}
