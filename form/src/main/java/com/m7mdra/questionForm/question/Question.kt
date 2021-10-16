package com.m7mdra.questionForm.question

import android.service.carrier.CarrierIdentifier


abstract class Question<T>(
    val questionType: QuestionType,
    val required: Boolean = false,
    open val identifier: String,
    val extraParams: Map<String, Any?>,
    val completed: Boolean = false
) {
    abstract var value: T
    abstract fun isValid(): Boolean
    abstract var hasError: Boolean
    abstract fun validate(): Boolean
    fun collect(): Map<String, Any?> {
        val map = mutableMapOf(
            "id" to identifier,
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

        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }


}
