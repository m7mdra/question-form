package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.row_video.view.*
import kotlinx.android.synthetic.main.row_video.view.errorTextView
import kotlinx.android.synthetic.main.row_video.view.rootLayout
import kotlinx.android.synthetic.main.row_video.view.stateLayout
import kotlinx.android.synthetic.main.row_video.view.titleTextView

class VideoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val captureOrPickVideoButton: MaterialButton = view.captureOrPickVideoButton
    val videoView: VideoView = view.videoImageView
    val titleTextView: TextView = view.titleTextView
    val errorTextView: TextView = view.errorTextView
    val messageTextView: TextView = view.messageTextView

    val playOrStopButton: ImageView = view.playButton
    val stateLayout: FrameLayout =view.stateLayout
    val rootView: FrameLayout = view.rootLayout

}