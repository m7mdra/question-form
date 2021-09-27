package com.m7mdra.questionForm.question

enum class QuestionType(value: Int,name:String) {
    Title(-1,"title"),
    Input(0,"input"),
    Dropdown(1,"drop"),
    Radio(2,"radio"),
    Check(3,"check"),
    Image(4,"image"),
    Audio(5,"audio"),
    Video(6,"video")


}