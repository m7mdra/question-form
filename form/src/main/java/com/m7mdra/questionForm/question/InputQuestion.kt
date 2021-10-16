package com.m7mdra.questionForm.question

import android.os.Parcelable
import android.text.InputType
import kotlinx.android.parcel.Parcelize

@Parcelize
class InputQuestion(
    title: String,
    id: String,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    hint: String = "",
    val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf(),
    done: Boolean = false, override var value: String? = null


) :
    Question<String?>(
        title,
        QuestionType.Input,
        id = id,
        required = mandatory,
        extraParams = params,
        done = done
    ), Parcelable {
    override var hasError: Boolean = false

    override fun validate(): Boolean {

        val valid = isValid()
        hasError = !valid
        return valid

    }

    override fun isValid(): Boolean {
        return if (required && !done) {
            value != null
        } else {
            true
        }
    }


    override fun update(value: String?) {

        this.value = value
    }

}
