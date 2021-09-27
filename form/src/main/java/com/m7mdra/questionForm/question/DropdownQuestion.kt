package com.m7mdra.questionForm.question

class DropdownQuestion(
    title: String,
    val entries: List<String>,
     id: String,
    private val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf()

) :
    Question<String?>(title, QuestionType.Dropdown, id = id, required = mandatory,extraParams = params) {
    override var hasError: Boolean = false
    override var value: String? = null

    override fun validate(): Boolean {
        val valid = isValid()
        hasError = !valid
        return valid
    }

    override fun isValid(): Boolean {
        return if (required) {
            value != null
        } else {
            true
        }
    }



    override fun update(value: String?) {
        this.value = value
    }
}
