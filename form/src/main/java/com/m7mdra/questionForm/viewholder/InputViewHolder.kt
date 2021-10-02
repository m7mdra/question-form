package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.row_input.view.*

class InputViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
    val textInputEditText: TextInputEditText = view.textInputEditText
    val errorTextView: TextView = view.errorTextView
    val submittedTextView: MaterialTextView =view.submittedTextView
    val rootView: FrameLayout = view.rootLayout
}
