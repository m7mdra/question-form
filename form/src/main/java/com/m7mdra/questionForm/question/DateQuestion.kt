package com.m7mdra.questionForm.question

import java.util.*

class DateQuestion(title: String, val dateTime: Boolean = false) :
    Question<Date?>(title, questionType = QuestionType.Date) {
    override var hasError: Boolean = false
    override var value: Date? = null
    override fun collect(): Date {
        return Date()
    }

    override fun validate(): Boolean {
        return value != null
    }

    override fun update(value: Date?) {
        this.value = value
    }

}