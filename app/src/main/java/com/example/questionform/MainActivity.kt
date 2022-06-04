package com.example.questionform

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.m7mdra.questionForm.*
import com.m7mdra.questionForm.question.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.random.Random


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

    private val videoRecordCallback: (result: Boolean) -> Unit = { success ->
        if (success) {
            questionAdapter.updatePickedVideo(videoFile.path, lastVideoPosition)

        }

    }
    private val imageCaptureCallback: (result: Boolean) -> Unit = { success ->
        if (success) {
            questionAdapter.updateImageAdapterAtPosition(imageFile.path, lastImagePosition)
        }
    }
    private lateinit var recordVideoLauncher: ActivityResultLauncher<File>
    private lateinit var captureImageLauncher: ActivityResultLauncher<File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        captureImageLauncher =
            registerForActivityResult(ActivityContractors.CaptureImage(), imageCaptureCallback)
        recordVideoLauncher =
            registerForActivityResult(ActivityContractors.RecordVideo(), videoRecordCallback)
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
                imageClickListener = { parentPosition, childPosition, question, image ->

                    image.log()
                },
                videoClickListener = { index, videoQuestion ->
                    videoQuestion.log()

                }
            )
        recyclerView.adapter = questionAdapter
        questionAdapter.addQuestions(
            listOf(
                InputQuestion(
                    id = "1",
                    title = "Title titleTitle titleTitle titleTitle titleTitle titleTitle titleTitle titleTitle titleTitle title",
                    status = QuestionStatus.Rejected,
                    mandatory = true,
                    message = "Message message"
                ),
                ImageQuestion(
                    id = "1",
                    title = "Title title",
                    status = QuestionStatus.Rejected,
                    mandatory = true,
                    message = "Message message",
                    callback = object : QuestionCallback {
                        override fun onChange(question: Question<*>) {
                            question.log()
                        }

                    }
                ),
                DropdownQuestion(
                    entries = listOf("Option 1", "Option 2", "Option 3"),
                    id = "1",
                    title = "Title title",
                    status = QuestionStatus.Rejected,
                    mandatory = true,
                    message = "Message message"
                ),
                AudioQuestion(
                    id = "3",
                    title = "Title title",
                    status = QuestionStatus.Rejected,
                    mandatory = true,
                    message = "Message message32131"
                ),
                CheckQuestion(
                    id = "4",
                    title = "Title title2",
                    message = "Hello this is a message for error",
                    status = QuestionStatus.Rejected,
                    mandatory = true, entries = listOf("Value one", "Value two")
                ),
                RadioQuestion(
                    id = "4",
                    title = "Title title2",
                    message = "Hello this is a message for error",
                    status = QuestionStatus.Rejected,
                    mandatory = true, entries = listOf("Value one", "Value two")
                ),
                VideoQuestion(
                    id = "4",
                    title = "Title title2",
                    message = "Hello this is a message for error",
                    status = QuestionStatus.Rejected,
                    mandatory = true,
                    value = "https://cdn.videvo.net/videvo_files/video/premium/video0261/large_watermarked/500_00300_preview.mp4"
                ),
                InputQuestion(
                    id = "2",
                    title = "Title title2",
                    status = QuestionStatus.Pending,
                    mandatory = true,
                ),
            )
        )

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.isSmoothScrollbarEnabled = true
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

//    private fun generateList(): List<Question<*>> {
//        val list = mutableListOf<Question<*>>()
//        repeat((0..10).count()) {
//            list.add(
//                CheckQuestion(
//                    title = faker.elderScrolls().quote(),
//                    entries = listOf(
//                        faker.elderScrolls().creature(),
//                        faker.elderScrolls().creature(),
//                        faker.elderScrolls().creature(),
//                        faker.elderScrolls().creature()
//                    ),
//                    status = QuestionStatus.random(),
//                    id = faker.crypto().md5(),
//                    mandatory = faker.bool().bool(),
//                    callback = this
//                )
//            )
//            val localValue = faker.howIMetYourMother().character()
//
//            list.add(
//                RadioQuestion(
//                    title = faker.gameOfThrones().character(),
//                    entries = listOf(
//                        value,
//                        faker.lordOfTheRings().character(),
//                        localValue
//                    ).shuffled(),
//                    id = id,
//                    mandatory = faker.bool().bool(),
//                    callback = this,
//                    value = localValue,
//                    status = QuestionStatus.random()
//
//                )
//            )
//            list.add(
//                DropdownQuestion(
//                    faker.friends().quote(), id = faker.crypto().md5(),
//                    mandatory = faker.bool().bool(),
//                    entries = listOf(
//                        faker.friends().character(),
//                        faker.friends().character(),
//                        faker.friends().character(),
//                        faker.friends().character()
//                    ),
//                    callback = this,
//                    status = QuestionStatus.random()
//
//                )
//            )
//            list.add(
//                ImageQuestion(
//                    faker.hobbit().quote(), id = faker.crypto().md5(),
//                    mandatory = faker.bool().bool(),
//
//                    callback = this,
//                    status = QuestionStatus.random()
//
//
//                )
//            )
//            list.add(
//                VideoQuestion(
//                    faker.backToTheFuture().quote(), id = faker.crypto().md5(),
//                    mandatory = faker.bool().bool(),
//                    callback = this,
//                    status = QuestionStatus.random()
//
//
//                )
//            )
//            list.add(
//                AudioQuestion(
//                    title = faker.gameOfThrones().quote(), id = faker.crypto().md5(),
//                    mandatory = faker.bool().bool(),
//                    callback = this,
//                    status = QuestionStatus.random()
//
//
//                )
//            )
//
//            list.add(
//                InputQuestion(
//                    faker.harryPotter().quote(),
//                    id = faker.crypto().md5(),
//                    mandatory = faker.bool().bool(),
//                    callback = this,
//                    status = QuestionStatus.random()
//
//
//                )
//            )
//        }
//        return list.shuffled()
//    }

    private lateinit var videoFile: File
    private lateinit var imageFile: File

    private fun dispatchVideoCaptureIntent() {

        val videoPath = File(filesDir, "videos")
        videoPath.mkdir()
        videoFile = File(videoPath, "vid${Date().time}.mp4")

        recordVideoLauncher.launch(videoFile)
    }

    private fun dispatchImageCaptureIntent() {
        val imagePath = File(filesDir, "images")
        imagePath.mkdir()
        imageFile = File(imagePath, "img${Date().time}.jpg")

        captureImageLauncher.launch(imageFile)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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



