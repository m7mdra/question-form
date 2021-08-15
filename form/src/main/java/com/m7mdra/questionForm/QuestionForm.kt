package com.m7mdra.questionForm

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView

class QuestionForm(private val activity: Activity, private val recyclerView: RecyclerView) {
    private val questionAdapter: QuestionAdapter = QuestionAdapter(listOf())

    init {
        recyclerView.adapter = questionAdapter
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }
}


