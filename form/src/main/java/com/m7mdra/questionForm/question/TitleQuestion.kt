package com.m7mdra.questionForm.question

class TitleQuestion(title: String) :
    Question<Unit>(title = title, questionType = QuestionType.Title, required = false, id = "",extraParams = mapOf()) {
    override var value: Unit = Unit

    override var hasError: Boolean = false

    override fun validate(): Boolean {
        return true
    }


    override fun update(value: Unit) {
        //never called
    }

    override fun isValid(): Boolean {
        return true
    }

}