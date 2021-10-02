package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_title.view.*

class TitleViewHolder(val view:View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
}