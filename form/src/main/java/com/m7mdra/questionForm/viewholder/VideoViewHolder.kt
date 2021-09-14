package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.row_video.view.*

class VideoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val captureOrPickVideoButton: MaterialButton = view.captureOrPickVideoButton
    val videoView: VideoView = view.videoView
    val titleTextView: TextView = view.titleTextView
    val errorTextView: TextView = view.errorTextView

    val playOrStopButton: ImageView = view.playOrStopButton


}