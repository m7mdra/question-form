package com.example.questionform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.questionform.QuestionType.*
import com.example.questionform.viewholder.*


class QuestionAdapter(
    private val list: List<Question<*>>,
    private val imagePickListener: () -> Unit = {},
    private val audioRecordListener: (Int) -> Unit = {}
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lastImagePickIndex = -1
    private val textWatchers = mutableMapOf<Int, TextInputEditTextWatcher>()
    private val imageAdapters = mutableMapOf<Int, ImageAdapter>()
    private val audioViewHolderIndexes = mutableListOf<Int>()
    private val audioHandlers = mutableMapOf<Int, Handler>()
    private val audioHandlersCallback = mutableMapOf<Int, Runnable>()
    private val mediaPlayers = mutableMapOf<Int, MediaPlayer>()

    fun clear() {
        textWatchers.values.forEach {
            it.removeWatcher()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Audio.ordinal -> AudioViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_audio, parent, false)
            )
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
            Audio -> Audio.ordinal
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Input.ordinal -> {
                val inputQuestion = list[position] as InputQuestion

                val inputViewHolder = holder as InputViewHolder
                inputViewHolder.titleTextView.text = list[position].title
                val textWatcher =
                    object : TextInputEditTextWatcher(inputViewHolder.textInputEditText) {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {

                        }

                        override fun afterTextChanged(s: Editable) {
                            inputQuestion.update(s.toString())
                        }

                    }
                textWatchers[position] = textWatcher

                inputViewHolder.textInputEditText.addTextChangedListener(textWatcher)
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
                radioGroup.setOnCheckedChangeListener { _, checkedId ->
                    entries.find { it.hashCode() == checkedId }.log()
                }
            }
            Check.ordinal -> {
                val checkViewHolder = holder as CheckViewHolder
                val checkQuestion = list[position] as CheckQuestion
                checkViewHolder.titleTextView.text = checkQuestion.title
                checkQuestion.entries.forEachIndexed { index, s ->
                    val checkBox = CheckBox(checkViewHolder.context)
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
            Audio.ordinal -> {
                "called".log()
                val audioViewHolder = holder as AudioViewHolder

                audioViewHolderIndexes.add(position)
                val mediaPlayer = MediaPlayer()
                val handler = Handler(Looper.getMainLooper())

                val runnable = object : Runnable {
                    override fun run() {
                        if (mediaPlayer.isPlaying) {
                            val playPosition: Int = mediaPlayer.currentPosition
                            val duration: Int = mediaPlayer.duration

                            if (duration > 0) {
                                val pos = 1000L * playPosition / duration
                                holder.recordProgress.progress = pos.toInt()
                            }
                            handler.postDelayed(this, 1000 - playPosition.toLong() % 1000)
                        }
                    }

                }
                mediaPlayers.replace(position,mediaPlayer)
                audioHandlers.replace(position,handler)
                audioHandlersCallback.replace(position,runnable)

                val question = list[position] as AudioQuestion
                audioViewHolder.titleTextView.text = question.title
                audioViewHolder.playOrStopButton.setOnClickListener {
//                    val audio = question.collect() ?: return@setOnClickListener
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                        holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_stop_24)

                    } else {
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource("https://freepd.com/music/Forest%20Frolic%20Loop.mp3")
                        mediaPlayer.prepareAsync()

                    }
                }
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                    handler.removeCallbacks(runnable)
                    handler.post(runnable)
                    holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_stop_24)
                }
                mediaPlayer.setOnCompletionListener {
                    holder.recordProgress.progress = 0
                    holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

                }
                audioViewHolder.recordAudioButton.text =
                    if (isAudioPermissionGranted(audioViewHolder.context)
                    )
                        "Record" else "grant permission"
                audioViewHolder.recordAudioButton.setOnClickListener {
                    if (isAudioPermissionGranted(audioViewHolder.context)) {

                    } else {
                        audioRecordListener.invoke(position)
                    }
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

    private fun isAudioPermissionGranted(context: Context) =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

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

    fun updateRecordAudioButtons() {
        audioViewHolderIndexes.forEach {
            notifyItemChanged(it)
        }
    }

}


val RecyclerView.ViewHolder.context: Context
    get() = this.itemView.context


