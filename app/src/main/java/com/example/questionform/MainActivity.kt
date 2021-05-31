package com.example.questionform

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.recyclerview.widget.DividerItemDecoration
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.m7mdra.questionForm.askForAudioPermission
import com.m7mdra.questionForm.askForCameraPermission
import com.m7mdra.questionForm.isAudioPermissionGranted
import com.m7mdra.questionForm.isCameraPermissionGranted
import com.m7mdra.questionForm.log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var questionAdapter: com.m7mdra.questionForm.QuestionAdapter
    private lateinit var arrayAdapter: ArrayAdapter<String>


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == com.m7mdra.questionForm.CAMERA_REQUEST_CODE) {
            if (isCameraPermissionGranted()) {
                dispatchVideoCaptureIntent()
            } else {
                Toast.makeText(this, "Camera permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == com.m7mdra.questionForm.RECORD_AUDIO_REQUEST_CODE) {
            if (isAudioPermissionGranted()) {
                questionAdapter.updateRecordAudioButtons()
                Toast.makeText(this, "you can record now.", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "permission not granted.", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private val audioRecordListener = { position: Int ->
        if (isAudioPermissionGranted()) {
            val intent = Intent(this, com.m7mdra.questionForm.RecordAudioActivity::class.java)
            intent.putExtra("position", position)
            startActivityForResult(intent, 321)
        } else {
            if (shouldShowRequestPermissionRationale(com.m7mdra.questionForm.RECORD_AUDIO_PERMISSION)) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Audio permission required.")
                    .setPositiveButton("grant") { _, _ ->
                        askForAudioPermission()
                    }
                    .create().show()
            } else {
                askForAudioPermission()
            }
        }
    }


    private val imagePickListener = {
        MaterialAlertDialogBuilder(this)
            .setTitle("Select image quality")
            .setAdapter(
                arrayAdapter
            ) { _, which ->
                val quality = when (which) {
                    0 -> 100
                    1 -> 60
                    2 -> 30
                    else -> 100
                }
                CropImage
                    .activity()
                    .setOutputCompressQuality(quality)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)
            }
            .create().show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        validateButton.setOnClickListener {
            questionAdapter.validate().log()
        }
        arrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1
        )
        arrayAdapter.addAll("High", "Medium", "Low")

        questionAdapter =
            com.m7mdra.questionForm.QuestionAdapter(list, imagePickListener, audioRecordListener, {
                if (isCameraPermissionGranted()) {
                    dispatchVideoCaptureIntent()
                } else {
                    askForCameraPermission()
                }
            })

        recyclerView.adapter = questionAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private lateinit var videoFile: File
    private fun dispatchVideoCaptureIntent() {
        try {
            Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { intent ->
                intent.resolveActivity(packageManager)?.also { _ ->
                    val newFile: File? = try {
                        val imagePath = File(filesDir, "videos")
                        imagePath.mkdir()
                        File.createTempFile("video${Date().time}", ".mp4", imagePath)

                    } catch (ex: IOException) {
                        finish()
                        null
                    }
                    if (newFile != null) {
                        videoFile = newFile
                    }
                    newFile?.also {
                        it.log()
                        val contentUri =
                            getUriForFile(this, packageName, it)
                        contentUri.log()
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                        startActivityForResult(intent, 43)
                    }
                }
            }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 43) {
            if (resultCode == RESULT_OK) {
                questionAdapter.updatePickedVideo(videoFile)
            }
        }
        if (requestCode == 321) {
            if (resultCode == RESULT_OK) {
                val recordFile: Uri? = data?.getParcelableExtra<Uri>("recordPath")
                val position = data?.getIntExtra("position", -1) ?: -1
                recordFile.log()
                position.log()
                questionAdapter.addRecordFile(recordFile, position)

            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                result?.also {
                    questionAdapter.updateImageAdapterAtPosition(
                        questionAdapter.lastImagePickIndex,
                        it.originalUri.toString()
                    )
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result!!.error
            }
        }
    }
}

val list = listOf(
    com.m7mdra.questionForm.VideoQuestion("Video question here."),
    com.m7mdra.questionForm.AudioQuestion("Record a summary of the condition of the power generator line 0"),
    com.m7mdra.questionForm.AudioQuestion("Record a summary of the condition of the power generator line 1"),
    com.m7mdra.questionForm.AudioQuestion("Record a summary of the condition of the power generator line 2"),
    com.m7mdra.questionForm.InputQuestion("What is the name of the security guard?"),
    com.m7mdra.questionForm.ImageQuestion(
        "Site panorama (8 photos) and shelters(4 photos) overview ",
        4,
        4
    ),
    com.m7mdra.questionForm.InputQuestion("What is the phone number of the security guard?"),
    com.m7mdra.questionForm.RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    com.m7mdra.questionForm.RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    com.m7mdra.questionForm.RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    com.m7mdra.questionForm.RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    com.m7mdra.questionForm.RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    com.m7mdra.questionForm.RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    com.m7mdra.questionForm.RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    com.m7mdra.questionForm.RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    com.m7mdra.questionForm.RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    com.m7mdra.questionForm.RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    com.m7mdra.questionForm.CheckQuestion(
        "Type of power source",
        listOf("Power line", "Generator", "Solar pales", "All above")
    ), com.m7mdra.questionForm.CheckQuestion(
        "Type of power source",
        listOf("Power line", "Generator", "Solar pales", "All above")
    ), com.m7mdra.questionForm.CheckQuestion(
        "Type of power source",
        listOf("Power line", "Generator", "Solar pales", "All above")
    ),
    com.m7mdra.questionForm.DropdownQuestion("Tower type", listOf("GSM", "2G", "3G", "3.75G", "4G"))
)