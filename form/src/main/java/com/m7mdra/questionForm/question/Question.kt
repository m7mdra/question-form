package com.m7mdra.questionForm.question


abstract class Question<T>(
    val title: String = "",
    val questionType: QuestionType,
    val error: String = "",
    val required: Boolean = false
) {
    abstract var value: T
    abstract var hasError: Boolean
    abstract fun validate(): Boolean
    abstract fun collect(): T
    abstract fun update(value: T)

}
