package com.m7mdra.questionForm

import android.text.InputType
import android.util.Log
import java.io.File

abstract class Question<T>(
    val title: String = "",
    val questionType: QuestionType,
    val error: String = "",
    val required: Boolean = false,
    val validationMode: ValidationMode = ValidationMode.Off

) {
    abstract var value: T
    abstract var hasError: Boolean
    abstract fun validate(): Boolean
    abstract fun collect(): T
    abstract fun update(value: T)

}

enum class QuestionType(value: Int) {
    Input(0),
    Dropdown(1),
    Radio(2),
    Check(3),
    Image(4),
    Audio(5),
    Video(6)

}


class ImageQuestion(title: String, private val maxInput: Int, private val minInput: Int) :
    Question<MutableList<String>>(title, QuestionType.Image) {
    override var hasError: Boolean = false
    override var value: MutableList<String> = mutableListOf()


    override fun validate(): Boolean {

        val size = value.size
        return size in (minInput + 1) until maxInput
    }

    override fun collect(): MutableList<String> {
        return value
    }

    override fun update(value: MutableList<String>) {
        this.value.addAll(value)
    }


}

class AudioQuestion(title: String) : Question<File?>(title, questionType = QuestionType.Audio) {
    override var value: File? = null

    override var hasError: Boolean = false
        get() {
            return !validate()
        }


    override fun validate(): Boolean {

        return value != null
    }

    override fun collect(): File? {
        return value
    }

    override fun update(value: File?) {
        this.value = value
    }

}

class VideoQuestion(title: String) : Question<File?>(title, questionType = QuestionType.Video) {
    override var value: File? = null
    override var hasError: Boolean = false

    override fun validate(): Boolean {
        return value != null
    }

    override fun collect(): File? {
        return value
    }

    override fun update(value: File?) {
        this.value = value
    }
}

class InputQuestion(
    title: String,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    hint: String = ""

) :
    Question<String?>(title, QuestionType.Input) {
    override var hasError: Boolean = false

    override var value: String? = null
    override fun validate(): Boolean {
        return value != null && value!!.isNotBlank() && value!!.isNotEmpty()
    }

    override fun collect(): String? {
        return value
    }

    override fun update(value: String?) {
        value.log()

        this.value = value
    }

}

class DropdownQuestion(title: String, val entries: List<String>) :
    Question<String>(title, QuestionType.Dropdown) {
    override var hasError: Boolean = false

    override var value: String = ""
    override fun validate(): Boolean {
        return value.isNotBlank() && value.isNotEmpty()
    }

    override fun collect(): String {
        return value
    }

    override fun update(value: String) {
        this.value = value
        this.value.log()
    }
}

class RadioQuestion(title: String, val entries: List<String>) :
    Question<String>(title, QuestionType.Radio) {
    override var hasError: Boolean = false

    override var value: String = ""
    override fun validate(): Boolean {
        return value.isNotBlank() && value.isNotEmpty()
    }

    override fun collect(): String {
        return value
    }

    override fun update(value: String) {
        this.value = value

    }

}

class CheckQuestion(title: String, val entries: List<String>) :
    Question<List<String>>(title, QuestionType.Check) {
    override var hasError: Boolean = false

    override var value: List<String> = listOf()
    var selectionMap = mutableMapOf<Int, String>()
    override fun validate(): Boolean {
        return value.isNotEmpty()
    }

    override fun collect(): List<String> {
        return selectionMap.values.toList()
    }

    override fun update(value: List<String>) {
        this.value = value
    }
}

fun Any?.log() {
    Log.d("MEGA", "$this")
}