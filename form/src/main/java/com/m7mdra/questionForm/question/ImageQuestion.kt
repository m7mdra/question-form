package com.m7mdra.questionForm.question

import android.os.Parcelable
import com.m7mdra.questionForm.log
import kotlinx.android.parcel.Parcelize

@Parcelize
class ImageQuestion(
    val title: String,
    val id: String,
    val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf(),
    val done: Boolean = false,
    override var value: MutableList<String> = mutableListOf()


) :
    Question<MutableList<String>>(
        QuestionType.Image,
        identifier = id,
        required = mandatory,
        extraParams = params,
        completed = done
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
        "UPDATED LIST WITH VALUE: $value ${isValid()} error: $hasError".log()
    }


}