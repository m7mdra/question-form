package com.m7mdra.questionForm.question

import android.os.Parcelable
import com.m7mdra.questionForm.log
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class ImageQuestion(
    val title: String,
    val id: String,
    val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf(),
    override var value: MutableList<String> = mutableListOf(),
    private val callback: @RawValue QuestionCallback? = null,
    override var status: QuestionStatus = QuestionStatus.Default


) :
    Question<MutableList<String>>(
        QuestionType.Image,
        identifier = id,
        required = mandatory,
        extraParams = params,
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
            value.isNotEmpty()
        } else {
            true
        }
    }


    override fun update(value: MutableList<String>) {
        super.update(this.value)
        this.value = value
    }

    fun removeChildAt(childIndex: Int) {
        value.removeAt(childIndex)
        super.update(value)
    }

}