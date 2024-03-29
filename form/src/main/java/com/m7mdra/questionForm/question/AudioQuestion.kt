package com.m7mdra.questionForm.question

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.io.File

@Parcelize
class AudioQuestion(
    override var value: String? = null,
    val title: String,
    val id: String,
    private val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf(),
    private val callback: @RawValue QuestionCallback? = null,
    override var status: QuestionStatus = QuestionStatus.Default,
    override var message: String = ""


) :
    Question<String?>(
        questionType = QuestionType.Audio,
        identifier = id,
        message = message,
        required = mandatory,
        extraParams = params.toMutableMap(),
        status = status,
        callback = callback
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
        super.update(value)
        this.value = value
    }

}
