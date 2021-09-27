package com.m7mdra.questionForm.question

class RadioQuestion(
    title: String, val entries: List<String>,  id: String,
    val mandatory: Boolean,
    private val params: Map<String, String> = mapOf()

) :
    Question<String?>(title, QuestionType.Radio, id = id, required = mandatory,extraParams = params) {
    override var value: String? = null
    override var hasError: Boolean = false

    override fun validate(): Boolean {
        val valid = isValid()
        hasError = !valid
        return valid

    }

    override fun isValid() = if (required) {
        value != null
    } else {
        true
    }



    override fun update(value: String?) {
        this.value = value

    }

}
