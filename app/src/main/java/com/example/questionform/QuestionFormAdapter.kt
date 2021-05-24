package com.example.questionform

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.questionform.QuestionType.*
import com.example.questionform.viewholder.*

class QuestionFormAdapter(
    private val list: List<Question<*>>,
    private val imagePickListener: () -> Unit = {}
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lastImagePickIndex = -1
    private val imageAdapters = mutableMapOf<Int, ImageAdapter>()
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
                        dropdownQuestion.update(dropdownQuestion.entries[index])
                    }
            }
            Radio.ordinal -> {
                val radioViewHolder = holder as RadioViewHolder
                val radioQuestion = list[position] as RadioQuestion
                radioViewHolder.titleTextView.text = radioQuestion.title
                val radioGroup = radioViewHolder.radioGroup
                val entries = radioQuestion.entries
                entries.forEach {
                    val radioButton = RadioButton(radioViewHolder.itemView.context)
                    radioButton.id = it.hashCode()
                    radioButton.text = it
                    radioGroup.addView(radioButton)
                }
                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    entries.find { it.hashCode() == checkedId }.log()
                }
            }
            Check.ordinal -> {
                val checkViewHolder = holder as CheckViewHolder
                val checkQuestion = list[position] as CheckQuestion
                checkViewHolder.titleTextView.text = checkQuestion.title
                checkQuestion.entries.forEachIndexed { index, s ->
                    val checkBox = CheckBox(checkViewHolder.itemView.context)
                    checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            checkQuestion.selectionMap[index] = s
                        } else {
                            checkQuestion.selectionMap.remove(index)
                        }
                        checkQuestion.selectionMap.log()
                    }
                    checkBox.text = s
                    checkBox.id = s.hashCode()
                    checkViewHolder.checkboxLayout.addView(checkBox)
                }

            }
            Image.ordinal -> {
                val imageViewHolder = holder as ImageViewHolder
                val imageQuestion = list[position] as ImageQuestion
                val adapterPosition = holder.adapterPosition
                imageViewHolder.imageButton.setOnClickListener {
                    lastImagePickIndex = adapterPosition
                    imagePickListener.invoke()
                }
                imageViewHolder.titleTextView.text = imageQuestion.title
                val imageAdapter = ImageAdapter()
                imageAdapters[adapterPosition] = imageAdapter
                imageViewHolder.imagesRecyclerView.adapter = imageAdapter

            }

        }
    }

    fun updateImageAdapterAtPosition(position: Int, uri: String) {
        val adapter: ImageAdapter = imageAdapters[position] ?: return
        adapter.add(uri)
        adapter.notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun validate(): Boolean {
        return list.all { it.validate() }
    }

    fun collect(): List<*> {
        return list.map { it.collect() }
    }

}




