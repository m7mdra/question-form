package com.m7mdra.questionForm.question

class RadioQuestion(
    title: String, val entries: List<String>, val id: String,
    val mandatory: Boolean = false
) :
    Question<String?>(title, QuestionType.Radio, id = id,required = mandatory) {
    override var hasError: Boolean = false

    override var value: String? = ""
    override fun validate(): Boolean {
        val nonNullValue: String = value ?: ""
        return nonNullValue.isNotBlank() && nonNullValue.isNotEmpty()
    }

    override fun collect(): Pair<String, String?> {
        return id to value
    }

    override fun update(value: String?) {
        this.value = value

    }

}
