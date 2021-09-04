package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_check.view.*
import kotlinx.android.synthetic.main.row_dropdown.view.*
import kotlinx.android.synthetic.main.row_dropdown.view.errorTextView
import kotlinx.android.synthetic.main.row_dropdown.view.titleTextView

class DropdownViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
    val autoCompleteTextView: AutoCompleteTextView = view.autoCompleteTextView
    val errorTextView: TextView = view.errorTextView

}