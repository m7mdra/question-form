package com.m7mdra.questionForm.question

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DropdownQuestion(
    val title: String,
    val entries: List<String>,
    val id: String,
    private val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf(),
    val done: Boolean = false,
    override var value: String? = null

) :
    Question<String?>(

        QuestionType.Dropdown,
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
            value != null
        } else {
            true
        }
    }


    override fun update(value: String?) {
        this.value = value
    }
}
