package com.example.questionform.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_input.view.*

class InputViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
}
