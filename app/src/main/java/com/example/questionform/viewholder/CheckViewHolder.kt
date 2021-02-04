package com.example.questionform.viewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_check.view.*

class CheckViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
    val checkboxLayout: LinearLayout = view.checkboxLayout
}