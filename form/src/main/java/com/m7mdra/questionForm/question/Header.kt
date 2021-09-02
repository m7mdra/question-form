package com.m7mdra.questionForm.question

class Header(title: String) :
    Question<Unit>(title = title, questionType = QuestionType.Title, required = false, id = "") {
    override var value: Unit = Unit
    override var hasError: Boolean = false


    override fun validate(): Boolean {
        return true
    }

    override fun collect(): Pair<String, Unit> {
        return "" to Unit
    }

    override fun update(value: Unit) {
        //never called
    }

}