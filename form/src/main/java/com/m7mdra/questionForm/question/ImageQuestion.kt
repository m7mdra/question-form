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
    val done: Boolean = false,
    override var value: MutableList<String> = mutableListOf(),
    private val callback:  @RawValue QuestionCallback? = null


) :
    Question<MutableList<String>>(
        QuestionType.Image,
        identifier = id,
        required = mandatory,
        extraParams = params,
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
        return if (required && !completed) {
            value.isNotEmpty()
        } else {
            true
        }
    }


    override fun update(value: MutableList<String>) {
        this.value.addAll(value)
        super.update(this.value)
        "UPDATED LIST WITH VALUE: $value ${isValid()} error: $hasError".log()
    }

}