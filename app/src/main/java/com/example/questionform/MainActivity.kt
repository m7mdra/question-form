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
import com.m7mdra.questionForm.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var arrayAdapter: ArrayAdapter<String>


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (isCameraPermissionGranted()) {
                Toast.makeText(this, "Camera permission granted, you can now complete the process", Toast.LENGTH_SHORT).show()
                questionAdapter.updateCameraAndVideoButton()
            } else {
                Toast.makeText(this, "Camera permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
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
            val intent = Intent(this, RecordAudioActivity::class.java)
            intent.putExtra("position", position)
            startActivityForResult(intent, 321)
        } else {
            if (shouldShowRequestPermissionRationale(RECORD_AUDIO_PERMISSION)) {
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
     if(isCameraPermissionGranted()){
         dispatchImageCaptureIntent()
     }else{
         askForCameraPermission()
     }
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
            QuestionAdapter(list, imagePickListener, audioRecordListener, {
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
    private lateinit var imageFile: File
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

    private fun dispatchImageCaptureIntent() {
        try {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                intent.resolveActivity(packageManager)?.also { _ ->
                    val newFile: File? = try {
                        val imagePath = File(filesDir, "images")
                        imagePath.mkdir()
                        File.createTempFile("image${Date().time}", ".jpeg", imagePath)

                    } catch (ex: IOException) {
                        finish()
                        null
                    }
                    if (newFile != null) {
                        imageFile = newFile
                    }
                    newFile?.also {
                        it.log()
                        val contentUri =
                            getUriForFile(this, packageName, it)
                        contentUri.log()
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                        startActivityForResult(intent, 42)
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
        if(requestCode==42){
            if(resultCode== RESULT_OK){
                questionAdapter.updateImageAdapterAtPosition(imageFile.path)
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
    VideoQuestion("Video question here."),
    AudioQuestion("Record a summary of the condition of the power generator line 0"),
    AudioQuestion("Record a summary of the condition of the power generator line 1"),
    AudioQuestion("Record a summary of the condition of the power generator line 2"),
    InputQuestion("What is the name of the security guard?"),
    ImageQuestion(
        "Site panorama (8 photos) and shelters(4 photos) overview ",
        4,
        4
    ),
    InputQuestion("What is the phone number of the security guard?"),
    RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    RadioQuestion(
        "Is there gasoil of container available ?",
        listOf("YES", "NO", "Maybe")
    ),
    CheckQuestion(
        "Type of power source",
        listOf("Power line", "Generator", "Solar pales", "All above")
    ), CheckQuestion(
        "Type of power source",
        listOf("Power line", "Generator", "Solar pales", "All above")
    ), CheckQuestion(
        "Type of power source",
        listOf("Power line", "Generator", "Solar pales", "All above")
    ),
    DropdownQuestion("Tower type", listOf("GSM", "2G", "3G", "3.75G", "4G"))
)