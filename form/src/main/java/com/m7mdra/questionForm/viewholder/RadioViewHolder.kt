package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_check.view.*
import kotlinx.android.synthetic.main.row_radio.view.*
import kotlinx.android.synthetic.main.row_radio.view.errorTextView
import kotlinx.android.synthetic.main.row_radio.view.titleTextView

class RadioViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
    val radioGroup: RadioGroup = view.radioGroup
    val errorTextView: TextView = view.errorTextView

}
