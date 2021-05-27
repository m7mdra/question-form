package com.example.questionform

import android.text.InputType
import android.util.Log
import java.io.File

abstract class Question<T>(
    val title: String = "",
    val questionType: QuestionType
) {
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
    Audio(5)
//    Video(6)

}


class ImageQuestion(title: String, private val maxInput: Int, private val minInput: Int) :
    Question<List<String>>(title, QuestionType.Image) {

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
    override fun validate(): Boolean {
        return audioUri != null
    }

    override fun collect(): File? {
        return audioUri
    }

    override fun update(value: File?) {
        this.audioUri = value
    }

}

class VideoQuestion()
class InputQuestion(
    title: String,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    hint: String = ""

) :
    Question<String>(title, QuestionType.Input) {
    private var value: String = ""
    override fun validate(): Boolean {
        return value.isNotBlank() && value.isNotEmpty()
    }

    override fun collect(): String {
        return value
    }

    override fun update(value: String) {
        value.log()

        this.value = value
    }

}

class DropdownQuestion(title: String, val entries: List<String>) :
    Question<String>(title, QuestionType.Dropdown) {
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