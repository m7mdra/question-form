package com.m7mdra.questionForm.question

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class InputQuestion(
    val title: String,
    val id: String,
    val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf(),
    override var value: String? = null,
    private val callback: @RawValue QuestionCallback? = null,
    override var status: QuestionStatus = QuestionStatus.Default

) :
    Question<String?>(
        QuestionType.Input,
        identifier = id,
        required = mandatory,
        extraParams = params.toMutableMap(),
        callback = callback,
        status = status
    ), Parcelable {
    override var hasError: Boolean = false

    override fun validate(): Boolean {

        val valid = isValid()
        hasError = !valid
        return valid

    }

    override fun isValid(): Boolean {
        return if (required && status.isNotPendingNorAccepted()) {
            value != null
        } else {
            true
        }
    }


    override fun update(value: String?) {
        if (ensureValuesAreTheSame(value))
            return
        super.update(value)
        this.value = value
    }

    private fun ensureValuesAreTheSame(newValue: String?): Boolean = newValue == this.value

}
