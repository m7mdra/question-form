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
    Question<List<String>>(title, QuestionType.Image) {
    override var hasError: Boolean = false


    private val images = mutableListOf<String>()
    override fun validate(): Boolean {

        val size = images.size
        return size in (minInput + 1) until maxInput
    }

    override fun collect(): List<String> {
        return images
    }

    override fun update(value: List<String>) {
        images.addAll(value)
    }


}

class AudioQuestion(title: String) : Question<File?>(title, questionType = QuestionType.Audio) {
    private var audioUri: File? = null
    override var hasError: Boolean = false
        get() {
            return !validate()
        }


    override fun validate(): Boolean {

        val predicate = audioUri != null
        return predicate
    }

    override fun collect(): File? {
        return audioUri
    }

    override fun update(value: File?) {
        this.audioUri = value
    }

}

class VideoQuestion(title: String) : Question<File?>(title, questionType = QuestionType.Video) {
    private var videoUri: File? = null
    override var hasError: Boolean = false

    override fun validate(): Boolean {
        return videoUri != null
    }

    override fun collect(): File? {
        return videoUri
    }

    override fun update(value: File?) {
        this.videoUri = value
    }
}

class InputQuestion(
    title: String,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    hint: String = ""

) :
    Question<String?>(title, QuestionType.Input) {
    override var hasError: Boolean = false

    private var value: String? = null
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

    private var selection: String = ""
    override fun validate(): Boolean {
        return selection.isNotBlank() && selection.isNotEmpty()
    }

    override fun collect(): String {
        return selection
    }

    override fun update(value: String) {
        this.selection = value
        selection.log()
    }
}

class RadioQuestion(title: String, val entries: List<String>) :
    Question<String>(title, QuestionType.Radio) {
    override var hasError: Boolean = false

    private var selection: String = ""
    override fun validate(): Boolean {
        return selection.isNotBlank() && selection.isNotEmpty()
    }

    override fun collect(): String {
        return selection
    }

    override fun update(value: String) {
        this.selection = value

    }

}

class CheckQuestion(title: String, val entries: List<String>) :
    Question<List<String>>(title, QuestionType.Check) {
    override var hasError: Boolean = false

    private var selection: List<String> = listOf()
    var selectionMap = mutableMapOf<Int, String>()
    override fun validate(): Boolean {
        return selection.isNotEmpty()
    }

    override fun collect(): List<String> {
        return selectionMap.values.toList()
    }

    override fun update(value: List<String>) {
        this.selection = value
    }
}

fun Any?.log() {
    Log.d("MEGA", "$this")
}