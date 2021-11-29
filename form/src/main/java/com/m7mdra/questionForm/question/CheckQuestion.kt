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
    override var value: List<String> = listOf(),
    private val callback: @RawValue QuestionCallback? = null,
    override var status: QuestionStatus = QuestionStatus.Default,
    override var message: String = ""



) :
    Question<List<String>>(
        QuestionType.Check,
        identifier = id,
        status = status,
        message = message,
        required = mandatory,
        extraParams = params.toMutableMap(),
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
        return if (required && status.isNotPendingNorAccepted()) {
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
