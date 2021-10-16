package com.m7mdra.questionForm.question

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TitleQuestion(val title: String) :
    Question<Unit>(
        questionType = QuestionType.Title,
        required = false,
        identifier = "",
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