package com.m7mdra.questionForm.question

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class VideoQuestion(
    val title: String,
    val id: String,
    val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf(),
    override var value: String? = null,
    private val callback: @RawValue QuestionCallback? = null,
    override var status: QuestionStatus = QuestionStatus.Default,
    override var message: String = ""


) : Question<String?>(
    questionType = QuestionType.Video,
    identifier = id,
    required = mandatory,
    extraParams = params.toMutableMap(),
    callback = callback,
    status = status,
    message = message
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
