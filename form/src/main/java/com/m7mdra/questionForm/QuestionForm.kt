package com.m7mdra.questionForm

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.m7mdra.questionForm.question.ImageQuestion
import com.m7mdra.questionForm.question.InputQuestion
import java.io.File
import java.io.IOException
import java.util.*

class QuestionForm(private val activity: Activity, private val recyclerView: RecyclerView) {
    private val questionAdapter by lazy {
        QuestionAdapter(
            listOf(),
            imagePickListener,
            audioRecordListener,
            videoPickListener
        )
    }
    fun build(){
        recyclerView.adapter = questionAdapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )

    }
    fun validate() {
        questionAdapter.validate()
    }


    private lateinit var videoFile: File
    private lateinit var imageFile: File

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(
                    activity,
                    "Camera permission granted, you can now complete the process",
                    Toast.LENGTH_SHORT
                ).show()
                questionAdapter.updateCameraAndVideoButton()
            } else {
                Toast.makeText(activity, "Camera permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                questionAdapter.updateRecordAudioButtons()
                Toast.makeText(activity, "you can record now.", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(activity, "permission not granted.", Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 43) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                questionAdapter.updatePickedVideo(videoFile)
            }
        }
        if (requestCode == 42) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                questionAdapter.updateImageAdapterAtPosition(imageFile.path)
            }
        }
        if (requestCode == 321) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val recordFile: Uri? = data?.getParcelableExtra<Uri>("recordPath")
                val position = data?.getIntExtra("position", -1) ?: -1

                questionAdapter.addRecordFile(recordFile, position)

            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                result?.also {
                    questionAdapter.updateImageAdapterAtPosition(
                        it.originalUri.toString()
                    )
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result!!.error
            }
        }
    }

    private val audioRecordListener = { position: Int ->
        if (activity.isAudioPermissionGranted()) {
            val intent = Intent(activity, RecordAudioActivity::class.java)
            intent.putExtra("position", position)
            activity.startActivityForResult(intent, 321)
        } else {
            if (activity.shouldShowRequestPermissionRationale(RECORD_AUDIO_PERMISSION)) {
                MaterialAlertDialogBuilder(activity)
                    .setTitle("Audio permission required.")
                    .setPositiveButton("grant") { _, _ ->
                        activity.askForAudioPermission()
                    }
                    .create().show()
            } else {
                activity.askForAudioPermission()
            }
        }
    }


    private val imagePickListener = {
        if (activity.isCameraPermissionGranted()) {
            dispatchImageCaptureIntent()
        } else {
            activity.askForCameraPermission()
        }
    }
    private val videoPickListener: (Int) -> Unit = {
        if (activity.isCameraPermissionGranted()) {
            dispatchVideoCaptureIntent()
        } else {
            activity.askForCameraPermission()
        }
    }

    private fun dispatchVideoCaptureIntent() {
        try {
            Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { intent ->
                intent.resolveActivity(activity.packageManager)?.also { _ ->
                    val newFile: File? = try {
                        val imagePath = File(activity.filesDir, "videos")
                        imagePath.mkdir()
                        File.createTempFile("video${Date().time}", ".mp4", imagePath)

                    } catch (ex: IOException) {
                        activity.finish()
                        null
                    }
                    if (newFile != null) {
                        videoFile = newFile
                    }
                    newFile?.also {
                        it.log()
                        val contentUri =
                            FileProvider.getUriForFile(activity, activity.packageName, it)
                        contentUri.log()
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                        activity.startActivityForResult(intent, 43)
                    }
                }
            }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    private fun dispatchImageCaptureIntent() {
        try {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                intent.resolveActivity(activity.packageManager)?.also { _ ->
                    val newFile: File? = try {
                        val imagePath = File(activity.filesDir, "images")
                        imagePath.mkdir()
                        File.createTempFile("image${Date().time}", ".jpeg", imagePath)

                    } catch (ex: IOException) {
                        activity.finish()
                        null
                    }
                    if (newFile != null) {
                        imageFile = newFile
                    }
                    newFile?.also {
                        it.log()
                        val contentUri =
                            FileProvider.getUriForFile(activity, activity.packageName, it)
                        contentUri.log()
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                        activity.startActivityForResult(intent, 42)
                    }
                }
            }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

}


