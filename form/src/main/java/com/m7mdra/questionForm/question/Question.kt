package com.m7mdra.questionForm.question


abstract class Question<T>(
    val questionType: QuestionType,
    val required: Boolean = false,
    open val identifier: String,
    val extraParams: MutableMap<String, String>,
    private val callback: QuestionCallback?,
    open var status:QuestionStatus
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

    open fun update(value: T) {
        this.value = value
        callback?.onChange(this)
    }

    fun addParams(map: Map<String,String>){
        extraParams.putAll(map)
    }

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

    override fun toString(): String {
        return "Question(questionType=$questionType, required=$required, identifier='$identifier', status=$status, value=$value, hasError=$hasError)"
    }


}


