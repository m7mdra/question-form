package com.m7mdra.questionForm.question

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CheckQuestion(
    title: String,
    val entries: List<String>,
    id: String,
    private val mandatory: Boolean = false,
    private val params: Map<String, String> = mapOf(),
    done: Boolean = false, override var value: List<String> = listOf()


) :
    Question<List<String>>(
        title, QuestionType.Check, id = id, required = mandatory, extraParams = params, done = done
    ),Parcelable {
    override var hasError: Boolean = false

    var selectionMap = mutableMapOf<Int, String>()

    override fun validate(): Boolean {
        val valid = isValid()
        hasError = !valid
        return valid

    }

    override fun isValid(): Boolean {
        return if (required && !done) {
            selectionMap.isNotEmpty()
        } else {
            true
        }
    }


    override fun update(value: List<String>) {
        this.value = value
    }
}
