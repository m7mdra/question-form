package com.example.questionform

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_record_audio.*
import java.io.File

class RecordAudioActivity : AppCompatActivity() {
    private val recordFile by lazy {
        val parent = cacheDir
        File(parent, "audio.mp3")
    }
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer
    private var didRecord = false
    private var isRecording = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_audio)
        mediaRecorder = MediaRecorder()
        mediaPlayer = MediaPlayer()
        playButton.disable()
        deleteButton.setOnClickListener {
            recordFile.delete()
            playButton.disable()
            recordDurationTextView.text = "00:00"
            didRecord = false
        }
        doneButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("recordPath", recordFile.toUri())
            intent.putExtra("position", this@RecordAudioActivity.intent.getIntExtra("position", -1))
            setResult(if (didRecord) Activity.RESULT_OK else Activity.RESULT_CANCELED, intent)
            finish()
        }
        recordButton.setOnClickListener {
            if (isAudioPermissionGranted()) {
                startRecording()
            } else {
                askForAudioPermission()
            }
        }
        playButton.setOnClickListener {
            if (didRecord) {
                playAudio()
            }
        }
    }

    private val progressCallback: Runnable = object : Runnable {
        override fun run() {

            if (mediaPlayer.isPlaying) {
                val pos = publishProgress()
                handler.postDelayed(this, 1000 - pos % 1000)
            }
        }
    }

    private fun publishProgress(): Long {


        val position: Int = mediaPlayer.currentPosition
        val duration: Int = mediaPlayer.duration

        if (duration > 0) {
            val pos = 1000L * position / duration
            recordProgress.progress = pos.toInt()
            recordDurationTextView.text = (position / 1000L).formatDuration()
        }



        return position.toLong()

    }

    private fun playAudio() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            recordProgress.progress = 0
            playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        } else {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(this, recordFile.toUri())
            mediaPlayer.prepareAsync()

            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                deleteButton.disable()
                handler.removeCallbacks(progressCallback)
                handler.post(progressCallback)
                playButton.setImageResource(R.drawable.ic_baseline_pause_24)
            }
            mediaPlayer.setOnCompletionListener {
                recordProgress.progress = 0
                deleteButton.enable()

                playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }
    }

    var recordTime = 0L
    private val handler: Handler
        get() = Handler(mainLooper)
    private val recordTimerRunnable = object : Runnable {
        override fun run() {
            "run tototototototototo run totototototot".log()
            if (isRecording) {
                recordTime += 1
                recordDurationTextView.text = recordTime.formatDuration()
                handler.postDelayed(this, 1000)
            } else {
                recordTime = 0L
                handler.removeCallbacks(this)
            }
        }

    }

    private fun updateRecordingView() {
        if (isRecording) {
            recordButton.setImageResource(R.drawable.ic_baseline_stop_24)
            playButton.disable()
            handler.removeCallbacks(recordTimerRunnable)
            handler.post(recordTimerRunnable)

        } else {
            handler.removeCallbacks(recordTimerRunnable)

            recordButton.setImageResource(R.drawable.ic_baseline_mic_24)
            playButton.enable()
        }
    }

    private fun startRecording() {

        try {
            if (!didRecord) {
                if (!isRecording) {
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

                    mediaRecorder.setAudioEncodingBitRate(16 * 44100);
                    mediaRecorder.setAudioSamplingRate(44100);
                    mediaRecorder.setOutputFile(recordFile.path)
                    mediaRecorder.prepare()
                    mediaRecorder.start()
                    isRecording = true
                    deleteButton.disable()

                    updateRecordingView()
                } else {
                    isRecording = false
                    mediaRecorder.stop()
                    updateRecordingView()
                    deleteButton.enable()

                    didRecord = true

                }
            } else {
                MaterialAlertDialogBuilder(this)

                    .setPositiveButton("Start recording") { dialog, _ ->
                        didRecord = false
                        startRecording()
                        dialog.dismiss()
                    }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()
            }
        } catch (error: Exception) {
            Toast.makeText(this, "failed to record.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (isAudioPermissionGranted()) {
                startRecording()
            }
        }
    }
}