package com.example.questionform

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.m7mdra.questionForm.*
import com.m7mdra.questionForm.question.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), QuestionCallback {
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var arrayAdapter: ArrayAdapter<String>
//    val faker = Faker()


    private val audioRecordListener: (AudioQuestion, Int) -> Unit = { _, position ->
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

    private var lastImagePosition = -1
    private var lastVideoPosition = -1
    private val imagePickListener: (ImageQuestion, Int) -> Unit = { _, position ->
        if (isCameraPermissionGranted()) {
            this.lastImagePosition = position
            dispatchImageCaptureIntent()
        } else {
            askForCameraPermission()
        }
    }
    private val videoPickListener: (VideoQuestion, Int) -> Unit = { _, position ->
        if (isCameraPermissionGranted()) {
            lastVideoPosition = position
            dispatchVideoCaptureIntent()

        } else {
            askForCameraPermission()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        validateButton.setOnClickListener {
            questionAdapter.validate()
        }
        collectButton.setOnClickListener {
            questionAdapter.collect().log()
        }
        arrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1
        )
        arrayAdapter.addAll("High", "Medium", "Low")

        questionAdapter =
            QuestionAdapter(
                this,
                imagePickListener,
                audioRecordListener,
                videoPickListener,
                imageClickListener = { parentPosition, childPosition, image ->

                    image.log()
                })
        recyclerView.adapter = questionAdapter
        val questions = mutableListOf<Question<*>>()
        (0..1000).forEach { _ ->
            questions.add( DropdownQuestion(
                entries = listOf("Option 1", "Option 2", "Option 3"),
                id = "${Random().nextInt()}",
                title = "Title title",
                status = QuestionStatus.Default,
                mandatory = Random().nextBoolean(),
                message = "Message message"
            ))
            questions .add( CheckQuestion(
                id = "${Random().nextInt()}",
                title = "Title title2",
                status = QuestionStatus.Default,
                mandatory = Random().nextBoolean(),
                entries = listOf("Value one", "Value two")
            ))
           questions.add( RadioQuestion(
                id = "${Random().nextInt()}",
                title = "Title title2",
                status = QuestionStatus.Default,
                mandatory = Random().nextBoolean(),
               entries = listOf("Value one", "Value two")
            ))
        }
        questions.shuffle()
        questionAdapter.addQuestions(
            questions
        )

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.isSmoothScrollbarEnabled = true
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
                questionAdapter.updatePickedVideo(videoFile.path, lastVideoPosition)
            }
        }
        if (requestCode == 42) {
            if (resultCode == RESULT_OK) {
                questionAdapter.updateImageAdapterAtPosition(imageFile.path, lastImagePosition)
            }
        }
        if (requestCode == 321) {
            if (resultCode == RESULT_OK) {
                val recordFile: Uri? = data?.getParcelableExtra<Uri>("recordPath")
                val position = data?.getIntExtra("position", -1) ?: -1
                questionAdapter.addRecordFile(recordFile?.path, position)

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(
                    this,
                    "Camera permission granted, you can now complete the process",
                    Toast.LENGTH_SHORT
                ).show()
                questionAdapter.updateCameraAndVideoButton()
            } else {
                Toast.makeText(this, "Camera permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                questionAdapter.updateRecordAudioButtons()
                Toast.makeText(this, "you can record now.", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "permission not granted.", Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun randomString(): String {
        return Random(0).nextInt().toString()
    }

    override fun onChange(question: Question<*>) {
        "Question#onChange($question)".log()
    }
}



