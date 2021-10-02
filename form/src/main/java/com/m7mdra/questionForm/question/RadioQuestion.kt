package com.m7mdra.questionForm.question

class RadioQuestion(
    title: String, val entries: List<String>, id: String,
    val mandatory: Boolean,
    private val params: Map<String, String> = mapOf(),
    done: Boolean = false

) :
    Question<String?>(
        title,
        QuestionType.Radio,
        id = id,
        required = mandatory,
        extraParams = params,
        done = done
    ) {
    override var value: String? = null
    override var hasError: Boolean = false

    override fun validate(): Boolean {
        val valid = isValid()
        hasError = !valid
        return valid

    }

    override fun isValid(): Boolean {
        return if (required && !done) {
            value != null
        } else {
            true
        }
    }


    override fun update(value: String?) {
        this.value = value

    }

}
