package com.mobitel.fes.ui.questionnaire

import android.text.InputType

abstract class Question(
    val title: String = "",
    val questionType: QuestionType
)

enum class QuestionType(value: Int) {
    Input(0),
    Dropdown(1),
    Radio(2),
    Check(3),
    Image(4),
    Audio(5),

}
class ImageQuestion(title: String) :Question(title,QuestionType.Image)
class AudioQuestion()
class VideoQuestion( )
class InputQuestion(
    title: String,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    hint: String = ""
) :
    Question(title, QuestionType.Input)

class DropdownQuestion(title: String, val entries: List<String>) :
    Question(title, QuestionType.Dropdown)

class RadioQuestion(title: String, val entries: List<String>) :
    Question(title, QuestionType.Radio)

class CheckQuestion(title: String, val entries: List<String>) :
    Question(title, QuestionType.Check)