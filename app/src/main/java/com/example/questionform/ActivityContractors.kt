package com.example.questionform

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import java.io.File

class ActivityContractors {
    companion object{
    }
     class CaptureImage() : ActivityResultContract<File, Boolean>() {

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {

            return resultCode == Activity.RESULT_OK
        }

        override fun createIntent(context: Context, input: File): Intent {
            return Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->

                val contentUri =
                    FileProvider.getUriForFile(context, context.packageName, input)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)

            }

        }
    }

     class RecordVideo() : ActivityResultContract<File, Boolean>() {
        override fun createIntent(context: Context, input: File): Intent {
            return Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { intent ->
                context.packageManager.resolveActivity(intent, PackageManager.GET_META_DATA)
                    ?.also { _ ->

                        val contentUri =
                            FileProvider.getUriForFile(context, context.packageName, input)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)


                    }
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            return resultCode == Activity.RESULT_OK
        }
    }

}