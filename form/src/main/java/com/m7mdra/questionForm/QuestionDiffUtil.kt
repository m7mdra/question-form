package com.m7mdra.questionForm

import androidx.recyclerview.widget.DiffUtil
import com.m7mdra.questionForm.question.Question

class QuestionDiffUtilCallback : DiffUtil.ItemCallback<Question<*>>() {
    override fun areItemsTheSame(oldItem: Question<*>, newItem: Question<*>): Boolean {
        return oldItem.identifier == newItem.identifier
    }

    override fun areContentsTheSame(oldItem: Question<*>, newItem: Question<*>): Boolean {
        return oldItem == newItem
    }


}