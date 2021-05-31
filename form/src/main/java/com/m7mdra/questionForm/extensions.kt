package com.m7mdra.questionForm

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView


val RecyclerView.ViewHolder.context: Context
    get() = this.itemView.context
 const val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
 const val CAMERA_PERMISSION = Manifest.permission.CAMERA
const val RECORD_AUDIO_REQUEST_CODE = 123
const val CAMERA_REQUEST_CODE = 124

 fun Context.isAudioPermissionGranted() =
    checkSelfPermission(RECORD_AUDIO_PERMISSION) == PackageManager.PERMISSION_GRANTED

 fun Context.isCameraPermissionGranted() =
    checkSelfPermission(CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED

 fun Activity.askForAudioPermission() {
    requestPermissions(arrayOf(RECORD_AUDIO_PERMISSION), RECORD_AUDIO_REQUEST_CODE)
}
 fun Activity.askForCameraPermission() {
    requestPermissions(arrayOf(CAMERA_PERMISSION), RECORD_AUDIO_REQUEST_CODE)
}

fun EditText.asString() = text.toString().trim()


fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}


fun ViewGroup.disableChildern() {
    children.forEach {
        it.disable()
    }
}
fun ViewGroup.enableChildern(){
    children.forEach {
        it.enable()
    }
}


fun AppCompatActivity.adjustScreen(){
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
}
fun Long.formatDuration(): String = DateUtils.formatElapsedTime(this)
