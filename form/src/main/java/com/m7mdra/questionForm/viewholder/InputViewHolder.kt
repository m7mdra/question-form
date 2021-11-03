package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.row_audio.view.*
import kotlinx.android.synthetic.main.row_input.view.*
import kotlinx.android.synthetic.main.row_input.view.errorTextView
import kotlinx.android.synthetic.main.row_input.view.rootLayout
import kotlinx.android.synthetic.main.row_input.view.stateLayout
import kotlinx.android.synthetic.main.row_input.view.titleTextView

class InputViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
    val textInputEditText: TextInputEditText = view.textInputEditText
    val errorTextView: TextView = view.errorTextView
    val stateLayout: FrameLayout =view.stateLayout
    val rootView: FrameLayout = view.rootLayout
}
