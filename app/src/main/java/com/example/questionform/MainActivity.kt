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
import androidx.core.net.toFile
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.javafaker.Faker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.m7mdra.questionForm.*
import com.m7mdra.questionForm.question.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.random.Random


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), QuestionCallback {
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var arrayAdapter: ArrayAdapter<String>


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

        val list = generateList()
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
        questionAdapter.addQuestions(list)

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.isSmoothScrollbarEnabled = true
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun generateList(): List<Question<*>> {
        val list = mutableListOf<Question<*>>()
        val faker = Faker()
        repeat((0..1).count()) {
            list.add(
                CheckQuestion(
                    title = faker.elderScrolls().quote(),
                    entries = listOf(
                        faker.elderScrolls().creature(),
                        faker.elderScrolls().creature(),
                        faker.elderScrolls().creature(),
                        faker.elderScrolls().creature()
                    ),
                    status = QuestionStatus.random(),
                    id = faker.crypto().md5(),
                    mandatory = faker.bool().bool(),
                    done = faker.bool().bool(),
                    callback = this
                )
            )
            val value = faker.howIMetYourMother().character()

                 list.add(
                     RadioQuestion(
                         title = faker.gameOfThrones().character(),
                         entries = listOf(
                             faker.hobbit().character(),
                             faker.howIMetYourMother().character(),
                             faker.lordOfTheRings().character(),
                             value
                         ).shuffled(),
                         id = faker.crypto().md5(),
                         mandatory = faker.bool().bool(),
                         callback = this,
                         value = value,
                         status = QuestionStatus.random()

                     )
                 )
            list.add(
                DropdownQuestion(
                    faker.friends().quote(), id = faker.crypto().md5(),
                    mandatory = faker.bool().bool(),
                    entries = listOf(
                        faker.friends().character(),
                        faker.friends().character(),
                        faker.friends().character(),
                        faker.friends().character()
                    ),
                    done = faker.bool().bool(),
                    callback = this,
                    status = QuestionStatus.random()

                )
            )
                 list.add(
                     ImageQuestion(
                         faker.hobbit().quote(), id = faker.crypto().md5(),
                         mandatory = faker.bool().bool(),
                         done = faker.bool().bool(),

                         callback = this,
                         status = QuestionStatus.random()


                     )
                 )
                 list.add(
                     VideoQuestion(
                         faker.backToTheFuture().quote(), id = faker.crypto().md5(),
                         mandatory = faker.bool().bool(),
                         done = faker.bool().bool(),
                         callback = this,
                         status = QuestionStatus.random()


                     )
                 )
                 list.add(
                     AudioQuestion(
                         title = faker.gameOfThrones().quote(), id = faker.crypto().md5(),
                         mandatory = faker.bool().bool(),
                         done = faker.bool().bool(),
                         callback = this,
                         status = QuestionStatus.random()


                     )
                 )

                 list.add(
                     InputQuestion(
                         faker.harryPotter().quote(),
                         id = faker.crypto().md5(),
                         mandatory = faker.bool().bool(),
                         done = faker.bool().bool(),
                         callback = this,
                         status = QuestionStatus.random()


                     )
                 )
        }
        return list.shuffled()
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
                questionAdapter.addRecordFile(recordFile?.toFile(), position)

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



