package com.m7mdra.questionForm.question

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TitleQuestion(title: String) :
    Question<Unit>(
        title = title,
        questionType = QuestionType.Title,
        required = false,
        id = "",
        extraParams = mapOf()
    ),
    Parcelable {
    override var value: Unit = Unit

    override var hasError: Boolean = false

    override fun validate(): Boolean {
        return true
    }


    override fun update(value: Unit) {
        //never called
    }

    override fun isValid(): Boolean {
        return true
    }

}