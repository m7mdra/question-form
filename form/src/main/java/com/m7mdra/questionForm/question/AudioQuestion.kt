package com.m7mdra.questionForm.question

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.io.File

@Parcelize
class AudioQuestion(
    override var value: File? = null,
    val title: String,
    val id: String,
    private val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf(),
    val done: Boolean = false,
    private val callback: @RawValue QuestionCallback? = null,
    override val status: QuestionStatus = QuestionStatus.Default


) :
    Question<File?>(
        questionType = QuestionType.Audio,
        identifier = id,
        required = mandatory,
        extraParams = params,
        status = status,
        completed = done,
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


    override fun update(value: File?) {
        super.update(value)
        this.value = value
    }

}
