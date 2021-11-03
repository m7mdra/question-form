package com.m7mdra.questionForm.question

import androidx.annotation.VisibleForTesting

enum class QuestionStatus(private val id: Int) {
    Accepted(1),
    Pending(2),
    Rejected(3),
    Default(4);

    fun isPending(): Boolean {
        return this == Pending
    }

    fun isRejected(): Boolean {
        return this == Rejected
    }

    fun isAccepted(): Boolean {
        return this == Accepted
    }

    fun isNotPendingNorAccepted(): Boolean {
        return !isPending() && !isAccepted()

    }

    fun isPendingOrAccepted(): Boolean {
        return isPending() || isAccepted()
    }

    companion object {
        @JvmStatic
        @VisibleForTesting
        fun random(): QuestionStatus {
            return values().firstOrNull { it.id == (1..4).random() } ?: Default
        }
    }
}