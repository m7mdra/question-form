package com.example.questionform

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private val recordAudioPermission = Manifest.permission.RECORD_AUDIO
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==123){
            if(isAudioPermissionGranted()){
                questionAdapter.updateRecordAudioButtons()
                Toast.makeText(this, "you can record now.", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this, "permission not granted.", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private val audioRecordListener = { position: Int ->
        if (isAudioPermissionGranted()) {
            Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show()
        } else {
            if (shouldShowRequestPermissionRationale(recordAudioPermission)) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Audio permission required.")
                    .setPositiveButton("grant"){_,_->
                        askForPermission()
                    }
                    .create().show()
            } else {
                askForPermission()
            }
        }
    }

    private fun isAudioPermissionGranted() =
        checkSelfPermission(recordAudioPermission) == PackageManager.PERMISSION_GRANTED

    private fun askForPermission() {
        requestPermissions(arrayOf(recordAudioPermission), 123)
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

        arrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1
        )
        arrayAdapter.addAll("High", "Medium", "Low")

        questionAdapter = QuestionAdapter(list, imagePickListener, audioRecordListener)

        recyclerView.adapter = questionAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
    AudioQuestion("Record a summary of the condition of the power generator line 0"),
    AudioQuestion("Record a summary of the condition of the power generator line 1"),
    AudioQuestion("Record a summary of the condition of the power generator line 2"),
    InputQuestion("What is the name of the security guard?"),
    ImageQuestion("Site panorama (8 photos) and shelters(4 photos) overview ", 4, 4),
    InputQuestion("What is the phone number of the security guard?"),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    CheckQuestion(
        "Type of power source",
        listOf("Power line", "Generator", "Solar pales", "All above")
    ),
    DropdownQuestion("Tower type", listOf("GSM", "2G", "3G", "3.75G", "4G"))
)