package com.example.questionform.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_row_image.view.*

class RowImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val selectedImageView: ImageView = view.selectedImageView
}