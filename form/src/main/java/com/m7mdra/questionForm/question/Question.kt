package com.m7mdra.questionForm.question



abstract class Question<T>(
    val title: String = "",
    val questionType: QuestionType,
    val error: String = "",
    val required: Boolean = false,
   private val id: String
) {
    abstract var value: T
    abstract var hasError: Boolean
    abstract fun validate(): Boolean
    abstract fun collect(): Pair<String,T>
    abstract fun update(value: T)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Question<*>

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}
