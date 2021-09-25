package com.m7mdra.questionForm.question

class CheckQuestion(
    title: String,
    val entries: List<String>,
    val id: String,
    private val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf()

) :
    Question<List<String>>(title, QuestionType.Check, id = id, required = mandatory,extraParams = params) {
    override var hasError: Boolean = false

    override var value: List<String> = listOf()
    var selectionMap = mutableMapOf<Int, String>()

    override fun validate(): Boolean {
        val valid = isValid()
        hasError = !valid
        return valid

    }

    override fun isValid() = if (required) {
        selectionMap.isNotEmpty()
    } else {
        true
    }


    override fun update(value: List<String>) {
        this.value = value
    }
}
