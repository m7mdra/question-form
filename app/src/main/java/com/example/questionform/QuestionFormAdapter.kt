package com.example.questionform

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.questionform.viewholder.*
import com.mobitel.fes.ui.questionnaire.*
import com.mobitel.fes.ui.questionnaire.QuestionType.*

class QuestionFormAdapter(private val list: List<Question>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Input.ordinal -> InputViewHolder(
                layoutInflater.inflate(
                    R.layout.row_input,
                    parent,
                    false
                )
            )
            Dropdown.ordinal -> DropdownViewHolder(
                layoutInflater.inflate(
                    R.layout.row_dropdown,
                    parent,
                    false
                )
            )
            Radio.ordinal -> RadioViewHolder(
                layoutInflater.inflate(
                    R.layout.row_radio,
                    parent,
                    false
                )
            )
            Check.ordinal -> CheckViewHolder(
                layoutInflater.inflate(
                    R.layout.row_check,
                    parent,
                    false
                )
            )
            Image.ordinal -> ImageViewHolder(
                layoutInflater.inflate(
                    R.layout.row_image,
                    parent,
                    false
                )
            )
            else -> throw  NullPointerException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val question = list[position]
        return when (question.questionType) {
            Input -> Input.ordinal
            Dropdown -> Dropdown.ordinal
            Radio -> Radio.ordinal
            Check -> Check.ordinal
            Image -> Image.ordinal
            else -> 0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Input.ordinal -> {
                (holder as InputViewHolder).titleTextView.text = list[position].title
            }
            Dropdown.ordinal -> {
                val dropdownViewHolder = holder as DropdownViewHolder
                val dropdownQuestion = list[position] as DropdownQuestion
                dropdownViewHolder.titleTextView.text = dropdownQuestion.title
                val autoCompleteTextView = dropdownViewHolder.autoCompleteTextView
                autoCompleteTextView.setAdapter(
                    ArrayAdapter<String>(
                        holder.itemView.context,
                        android.R.layout.simple_dropdown_item_1line,
                        dropdownQuestion.entries
                    )
                )
                autoCompleteTextView.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, index, _ ->
                        autoCompleteTextView.setText(dropdownQuestion.entries[index], false)
                    }
            }
            Radio.ordinal -> {
                val radioViewHolder = holder as RadioViewHolder
                val radioQuestion = list[position] as RadioQuestion
                radioViewHolder.titleTextView.text = radioQuestion.title
                radioQuestion.entries.forEach {
                    val radioButton = RadioButton(radioViewHolder.itemView.context)
                    radioButton.text = it
                    radioViewHolder.radioGroup.addView(radioButton)
                }
            }
            Check.ordinal -> {
                val checkViewHolder = holder as CheckViewHolder
                val checkQuestion = list[position] as CheckQuestion
                checkViewHolder.titleTextView.text = checkQuestion.title
                checkQuestion.entries.forEach {
                    val checkBox = CheckBox(checkViewHolder.itemView.context)
                    checkBox.text = it
                    checkViewHolder.checkboxLayout.addView(checkBox)
                }
            }
            Image.ordinal -> {
                val imageViewHolder = holder as ImageViewHolder
                val imageQuestion = list[position] as ImageQuestion

                imageViewHolder.titleTextView.text = imageQuestion.title

            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}




