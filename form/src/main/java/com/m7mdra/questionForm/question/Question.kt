package com.m7mdra.questionForm.question


abstract class Question<T>(
    val title: String = "",
    val questionType: QuestionType,
    val required: Boolean = false,
    open val id: String,
    val extraParams: Map<String, Any?>
) {
    abstract var value: T
    abstract fun isValid(): Boolean
    abstract var hasError: Boolean
    abstract fun validate(): Boolean
    fun collect(): Map<String, Any?> {
        val map = mutableMapOf(
            "id" to id,
            "value" to value,
            "type" to questionType.name
        )
        map.putAll(extraParams)
        return map
    }

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
