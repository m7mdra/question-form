package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.row_image.view.*

class ImageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.titleTextView
    val imageButton: MaterialButton = view.captureImageButton
    val imagesRecyclerView: RecyclerView = view.imagesRecyclerView

}