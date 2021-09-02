package com.example.questionform

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.m7mdra.questionForm.QuestionAdapter
import com.m7mdra.questionForm.log
import com.m7mdra.questionForm.question.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var arrayAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        validateButton.setOnClickListener {
            questionAdapter.collect().log()
        }
        val list = listOf(
            TitleQuestion("Test title 1"),
            RadioQuestion(
                "number of Radio question",
                entries = listOf("entry 1", "entry 2", "entry 3"),
                id = randomString()
            ),
            ImageQuestion("Test image question", 1, 1, randomString()),
            VideoQuestion("Test video question", randomString()),
            AudioQuestion("Test audio question", randomString()),
            DropdownQuestion(
                "Test audio question",
                listOf("option 1", "option 2", "option 3"),
                randomString()
            ),
            InputQuestion("Test input question",id = randomString()),
            CheckQuestion(
                "number of Radio question",
                entries = listOf("entry 1", "entry 2", "entry 3"),
                id = randomString()
            ),
            TitleQuestion("Test title"),
            RadioQuestion(
                "number of Radio question",
                entries = listOf("entry 1", "entry 2", "entry 3"),
                id = randomString()
            ),
            ImageQuestion("Test image question", 1, 1, randomString()),
            VideoQuestion("Test video question", randomString()),
            AudioQuestion("Test audio question", randomString()),
            DropdownQuestion(
                "Test audio question",
                listOf("option 1", "option 2", "option 3"),
                randomString()
            ),
            InputQuestion("Test input question",id = randomString()),
            CheckQuestion(
                "number of Radio question",
                entries = listOf("entry 1", "entry 2", "entry 3"),
                id = randomString()
            )
        )
        questionAdapter = QuestionAdapter(list)
        recyclerView.adapter = questionAdapter

    }

    private fun randomString() = Random().nextInt().toString()



}
