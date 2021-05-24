package com.example.questionform

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var questionFormAdapter: QuestionFormAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1
        )
        adapter.addAll("High", "Medium", "Low")

        questionFormAdapter = QuestionFormAdapter(list) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Select image quality")
                .setAdapter(
                    adapter
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
        recyclerView.adapter = questionFormAdapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                result?.also {
                    questionFormAdapter.updateImageAdapterAtPosition(
                        questionFormAdapter.lastImagePickIndex,
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
    InputQuestion("What is the name of the security guard?"),
    ImageQuestion("Site panorama (8 photos) and shelters(4 photos) overview ", 4, 4),
    InputQuestion("What is the phone number of the security guard?"),
    RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
    CheckQuestion(
        "Type of power source",
        listOf("Power line", "Generator", "Solar pales", "All above")
    ),
    DropdownQuestion("Tower type", listOf("GSM", "2G", "3G", "3.75G", "4G"))
)