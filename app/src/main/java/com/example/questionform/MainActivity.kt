package com.example.questionform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.adapter = QuestionFormAdapter(
            listOf(
                InputQuestion("What is the name of the security guard?"),
                ImageQuestion("Site panorama (8 photos) and shelters(4 photos) overview "),
                InputQuestion("What is the phone number of the security guard?"),
                RadioQuestion("Is there gasoil of container available ?", listOf("YES", "NO")),
                CheckQuestion(
                    "Type of power source",
                    listOf("Power line", "Generator", "Solar pales", "All above")
                ),
                DropdownQuestion("Tower type", listOf("GSM", "2G", "3G", "3.75G", "4G"))
            )
        )
    }
}