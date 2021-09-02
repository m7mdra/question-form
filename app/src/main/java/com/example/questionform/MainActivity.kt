package com.example.questionform

import android.app.Activity
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
import com.canhub.cropper.CropImage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.m7mdra.questionForm.*
import com.m7mdra.questionForm.question.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private val form by lazy {
        QuestionForm(this, recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        form.build()

        validateButton.setOnClickListener {
            form.validate()
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        form.onActivityResult(requestCode,resultCode,data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        form.onRequestPermissionsResult(requestCode,permissions,grantResults)

    }
}
