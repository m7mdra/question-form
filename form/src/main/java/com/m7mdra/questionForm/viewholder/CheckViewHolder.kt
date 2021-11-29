package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_check.view.*
import kotlinx.android.synthetic.main.row_check.view.errorTextView
import kotlinx.android.synthetic.main.row_check.view.messageTextView
import kotlinx.android.synthetic.main.row_check.view.rootLayout
import kotlinx.android.synthetic.main.row_check.view.stateLayout
import kotlinx.android.synthetic.main.row_check.view.titleTextView

class CheckViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
    val checkboxLayout: LinearLayout = view.checkboxLayout
    val errorTextView: TextView = view.errorTextView
    val stateLayout: FrameLayout =view.stateLayout
    val rootView: FrameLayout = view.rootLayout
    val messageTextView: TextView = view.messageTextView

}