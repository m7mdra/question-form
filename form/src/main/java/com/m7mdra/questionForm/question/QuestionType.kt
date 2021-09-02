package com.m7mdra.questionForm.question

enum class QuestionType(value: Int) {
    Title(-1),
    Input(0),
    Dropdown(1),
    Radio(2),
    Check(3),
    Image(4),
    Audio(5),
    Video(6)


}