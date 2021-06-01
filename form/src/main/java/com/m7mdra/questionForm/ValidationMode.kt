package com.m7mdra.questionForm

enum class ValidationMode {
    Auto,
    Off
}

fun ValidationMode.isOff(): Boolean {
    return this == ValidationMode.Off
}