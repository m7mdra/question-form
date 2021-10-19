package com.m7mdra.questionForm.question

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class CheckQuestion(
    val title: String,
    val entries: List<String>,
    val id: String,
    private val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf(),
    val done: Boolean = false,
    override var value: List<String> = listOf(),
    private val callback: @RawValue QuestionCallback? = null


) :
    Question<List<String>>(
        QuestionType.Check,
        identifier = id,
        required = mandatory,
        extraParams = params,
        completed = done,
        callback = callback
    ), Parcelable {
    override var hasError: Boolean = false

    var selectionMap = mutableMapOf<Int, String>()

    override fun validate(): Boolean {
        val valid = isValid()
        hasError = !valid
        return valid

    }

    override fun isValid(): Boolean {
        return if (required && !done) {
            selectionMap.isNotEmpty()
        } else {
            true
        }
    }


    override fun update(value: List<String>) {
        val toList = selectionMap.values.toList()
        super.update(toList)
        this.value = toList

    }
}
