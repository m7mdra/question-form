package com.m7mdra.questionForm.viewholder

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.row_audio.view.*


class AudioViewHolder(val view:View)  : RecyclerView.ViewHolder(view){

    val titleTextView: TextView = view.titleTextView
    val recordProgress: LinearProgressIndicator = view.recordProgress
    val recordDurationTextView: TextView = view.recordDurationTextView
    val recordAudioButton: MaterialButton = view.recordAudioButton
    val playOrStopButton: ImageButton = view.playOrStopButton
    val errorTextView: TextView = view.errorTextView
    val stateLayout: FrameLayout =view.stateLayout
    val messageTextView: TextView = view.messageTextView
    val rootView: FrameLayout = view.rootLayout
}