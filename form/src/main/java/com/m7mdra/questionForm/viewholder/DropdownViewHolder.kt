package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.row_dropdown.view.*
import kotlinx.android.synthetic.main.row_dropdown.view.errorTextView
import kotlinx.android.synthetic.main.row_dropdown.view.messageTextView
import kotlinx.android.synthetic.main.row_dropdown.view.rootLayout
import kotlinx.android.synthetic.main.row_dropdown.view.stateLayout
import kotlinx.android.synthetic.main.row_dropdown.view.titleTextView

class DropdownViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
    val autoCompleteTextView: AutoCompleteTextView = view.autoCompleteTextView
    val autoCompleteTextViewLayout: TextInputLayout = view.autoCompleteTextViewLayout
    val errorTextView: TextView = view.errorTextView
    val stateLayout: FrameLayout =view.stateLayout
    val rootView: FrameLayout = view.rootLayout
    val messageTextView: TextView = view.messageTextView

}