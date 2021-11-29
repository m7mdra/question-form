package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.row_image.view.*
import kotlinx.android.synthetic.main.row_image.view.errorTextView
import kotlinx.android.synthetic.main.row_image.view.messageTextView
import kotlinx.android.synthetic.main.row_image.view.rootLayout
import kotlinx.android.synthetic.main.row_image.view.stateLayout
import kotlinx.android.synthetic.main.row_image.view.titleTextView

class ImageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
    val imageButton: MaterialButton = view.captureImageButton
    val imagesRecyclerView: RecyclerView = view.imagesRecyclerView
    val errorTextView: TextView = view.errorTextView
    val stateLayout: FrameLayout =view.stateLayout
    val rootView: FrameLayout = view.rootLayout
    val messageTextView: TextView = view.messageTextView

}