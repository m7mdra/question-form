package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.row_date.view.*

class DateVideoHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
    val dateValueTextView: TextView = view.dateValueTextView
    val pickDateButton: MaterialButton = view.pickDateButton
}