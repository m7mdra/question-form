package com.m7mdra.questionForm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.m7mdra.questionForm.question.*
import com.m7mdra.questionForm.question.QuestionType.*
import com.m7mdra.questionForm.viewholder.*
import java.io.File


class QuestionAdapter(
    private val context: Context,
    private val list: List<Question<*>>,
    private val imagePickListener: () -> Unit = {},
    private val audioRecordListener: (Int) -> Unit = {},
    private val videoPickListener: (Int) -> Unit = {},
    private val imageClickListener: (Int, Int, String) -> Unit = { _, _, _ -> }
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastImagePickIndex = -1
    private var lastImageVideoIndex = -1

    private val textWatchers = mutableMapOf<Int, TextInputEditTextWatcher>()
    private val dropDownListener = mutableMapOf<Int, AdapterView.OnItemClickListener>()
    private val imageAdapters = mutableMapOf<Int, ImageAdapter>()

    private val audioViewHolderIndexes = mutableListOf<Int>()
    private val mediaViewHolderIndexes = mutableListOf<Int>()
    private val audioHandlers = mutableMapOf<Int, Handler>()
    private val audioHandlersCallback = mutableMapOf<Int, Runnable>()
    private val mediaPlayers = mutableMapOf<Int, MediaPlayer>()

    fun clear() {
        mediaPlayers.forEach {
            val mediaPlayer = it.value
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        audioHandlers.forEach {
            val runnable = audioHandlersCallback[it.key]
            if (runnable != null)
                it.value.removeCallbacks(runnable)
        }
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
            Video.ordinal -> VideoViewHolder(
                layoutInflater.inflate(
                    R.layout.row_video,
                    parent,
                    false
                )
            )
            Title.ordinal -> TitleViewHolder(
                layoutInflater.inflate(
                    R.layout.row_title,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].questionType.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Title.ordinal -> {
                val holder = holder as TitleViewHolder
                holder.titleTextView.text = list[position].title
            }
            Input.ordinal -> {
                val question = list[position] as InputQuestion

                val holder = holder as InputViewHolder
                holder.errorTextView.visibility = if (question.hasError) View.VISIBLE else View.GONE

                holder.titleTextView.text =
                    titleWithRedAsterisk(question.required, question.title, position)
                if (question.value != null)
                    holder.textInputEditText.setText(question.value)
                val textWatcher =
                    object : TextInputEditTextWatcher(holder.textInputEditText) {
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
                            if (s.isNotEmpty())
                                question.update(s.toString())
                        }

                    }
                textWatchers[position] = textWatcher

                holder.textInputEditText.addTextChangedListener(textWatcher)
            }
            Dropdown.ordinal -> {
                val holder = holder as DropdownViewHolder
                val question = list[position] as DropdownQuestion
                holder.titleTextView.text =
                    titleWithRedAsterisk(question.required, question.title, position)

                val autoCompleteTextView = holder.autoCompleteTextView
                holder.errorTextView.visibility = if (question.hasError) View.VISIBLE else View.GONE
                autoCompleteTextView.setText(question.value, false)
                autoCompleteTextView.setAdapter(
                    ArrayAdapter<String>(
                        holder.itemView.context,
                        android.R.layout.simple_dropdown_item_1line,
                        question.entries
                    )
                )
                val onItemClickListener = AdapterView.OnItemClickListener { _, _, index, _ ->
                    autoCompleteTextView.setText(question.entries[index], false)
                    question.update(question.entries[index])

                }
                autoCompleteTextView.onItemClickListener = onItemClickListener
                dropDownListener[position] = onItemClickListener
            }
            Radio.ordinal -> {
                val holder = holder as RadioViewHolder
                val question = list[position] as RadioQuestion
                holder.errorTextView.visibility = if (question.hasError) View.VISIBLE else View.GONE

                holder.titleTextView.text =
                    titleWithRedAsterisk(question.required, question.title, position)

                val radioGroup = holder.radioGroup
                val entries = question.entries
                entries.forEach {
                    val radioButton = RadioButton(holder.context)
                    radioButton.id = it.hashCode()
                    radioButton.text = it
                    radioButton.isChecked =
                        question.value.hashCode() == it.hashCode()
                    radioGroup.addView(radioButton)
                    radioButton.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked)
                            question.update(it)
                    }
                }

            }
            Check.ordinal -> {
                val holder = holder as CheckViewHolder
                val question = list[position] as CheckQuestion
                holder.titleTextView.text =
                    titleWithRedAsterisk(question.required, question.title, position)

                holder.errorTextView.visibility = if (question.hasError) View.VISIBLE else View.GONE
                question.entries.forEachIndexed { index, s ->
                    val checkBox = CheckBox(holder.context)
                    checkBox.isChecked = question.selectionMap.containsKey(index)
                    checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            question.selectionMap[index] = s
                        } else {
                            question.selectionMap.remove(index)
                        }

                    }
                    checkBox.text = s
                    checkBox.id = s.hashCode()
                    holder.checkboxLayout.addView(checkBox)
                }

            }
            Audio.ordinal -> {

                val holder = holder as AudioViewHolder
                val question = list[position] as AudioQuestion

                audioViewHolderIndexes.add(position)
                val mediaPlayer = MediaPlayer()
                holder.errorTextView.visibility = if (question.hasError) View.VISIBLE else View.GONE

                val handler = Handler(Looper.getMainLooper())

                val runnable = object : Runnable {
                    override fun run() {
                        if (mediaPlayer.isPlaying) {
                            val playPosition: Int = mediaPlayer.currentPosition
                            val duration: Int = mediaPlayer.duration

                            if (duration > 0) {
                                val pos = 1000L * playPosition / duration
                                holder.recordProgress.progress = pos.toInt()
                                holder.recordDurationTextView.text =
                                    (playPosition / 1000L).formatDuration()
                            }
                            handler.postDelayed(this, 1000 - playPosition.toLong() % 1000)
                        }
                    }

                }
                mediaPlayers[position] = mediaPlayer
                audioHandlers[position] = handler
                audioHandlersCallback[position] = runnable

                holder.errorTextView.visibility = if (question.hasError) View.VISIBLE else View.GONE
                holder.titleTextView.text =

                    titleWithRedAsterisk(question.required, question.title, position)
                holder.playOrStopButton.isEnabled = question.value != null
                holder.playOrStopButton.setOnClickListener {
                    val audio = question.collect().second ?: return@setOnClickListener
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        holder.recordDurationTextView.text = "00:00"
                        holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                        holder.recordProgress.progress = 0
                    } else {
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(audio.path)
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
                    holder.recordDurationTextView.text = "00:00"
                    holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

                }
                holder.recordAudioButton.text =
                    if (isAudioPermissionGranted(holder.context)
                    )
                        "Record" else "grant permission"
                holder.recordAudioButton.setOnClickListener {

                    audioRecordListener.invoke(position)

                }
            }
            Image.ordinal -> {
                mediaViewHolderIndexes.add(position)

                val holder = holder as ImageViewHolder
                val question = list[position] as ImageQuestion
                val adapterPosition = holder.adapterPosition
                val cameraPermissionGranted = holder.context.isCameraPermissionGranted()
                holder.errorTextView.visibility = if (question.hasError) View.VISIBLE else View.GONE

                holder.imageButton.text =
                    if (cameraPermissionGranted) "Capture image" else "Grant permission"
                holder.imageButton.setOnClickListener {
                    lastImagePickIndex = adapterPosition
                    imagePickListener.invoke()
                }

                holder.titleTextView.text =
                    titleWithRedAsterisk(question.mandatory, question.title, position)
                val imageAdapter = ImageAdapter { childPosition, image ->
                    imageClickListener.invoke(position, childPosition, image)
                }

                imageAdapters[adapterPosition] = imageAdapter
                if (question.value.isNotEmpty())
                    imageAdapter.addAll(question.value)
                holder.imagesRecyclerView.adapter = imageAdapter

            }
            Video.ordinal -> {
                mediaViewHolderIndexes.add(position)
                val holder = holder as VideoViewHolder
                val question = list[position] as VideoQuestion
                val videoView = holder.videoView
                holder.errorTextView.visibility = if (question.hasError) View.VISIBLE else View.GONE

                val cameraPermissionGranted = holder.context.isCameraPermissionGranted()
                holder.titleTextView.text =
                    titleWithRedAsterisk(question.mandatory, question.title, position)
                holder.captureOrPickVideoButton.text =
                    if (cameraPermissionGranted) "Record video" else "Grant permission"
                holder.captureOrPickVideoButton.setOnClickListener {
                    lastImageVideoIndex = position
                    videoPickListener.invoke(position)
                }


                val file = question.collect().second
                if (file != null) {
                    videoView.show()
                    videoView.setVideoURI(file.toUri())
                }
                holder.playOrStopButton.setOnClickListener {
                    if (videoView.isPlaying) {
                        holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                        videoView.pause()
                    } else {
                        holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                        videoView.start()
                    }
                }
                videoView.setOnCompletionListener {
                    holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)

                }
                videoView.setOnPreparedListener {
                    holder.playOrStopButton.show()
                    holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }

        }
    }

    private fun isAudioPermissionGranted(context: Context) =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

    fun updateImageAdapterAtPosition(uri: String) {
        val adapter: ImageAdapter = imageAdapters[lastImagePickIndex] ?: return
        (list[lastImagePickIndex] as ImageQuestion).update(mutableListOf(uri))
        adapter.add(uri)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun validate(): Boolean {
        list.filterIsInstance<InputQuestion>()
            .forEach {
                "V:${it.validate()} R:${it.required} v:${it.value}".log()
            }
        notifyErrors()
        return list.all { it.validate() }
    }

    private var attachedRecyclerView: RecyclerView? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        attachedRecyclerView = null
    }

    private fun notifyErrors() {

        post {
            val first = list.firstOrNull { !it.validate() && it.required } ?: return@post
            val indexOfFirstError = list.indexOf(first)
            "$indexOfFirstError first error index".log()
            if (indexOfFirstError != -1) {
                attachedRecyclerView?.smoothSnapToPosition(indexOfFirstError)
            }
        }
/*        val linearLayoutManager = attachedRecyclerView?.layoutManager as LinearLayoutManager
        val range = visibleRange(linearLayoutManager)
        "visible children range: $range".log()
        attachedRecyclerView?.post{
            range.forEach {
                if(!list[it].validate() && list[it].required){
                    notifyItemChanged(it)
                }
            }
        }*/
        post {
            list.filter { !it.validate() && it.required }.forEachIndexed { index, _ ->
                notifyItemChanged(index)
            }
        }
    }

    private fun post(block: () -> Unit) {
        attachedRecyclerView?.post(block)
    }

    private fun visibleRange(linearLayoutManager: LinearLayoutManager): IntRange {
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
        return IntRange(firstVisibleItemPosition, lastVisibleItemPosition)
    }

    fun collect(): List<Pair<String, *>> {
        return list.map { it.collect() }
    }

    fun updateRecordAudioButtons() {
        audioViewHolderIndexes.forEach {
            notifyItemChanged(it)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        val adapterPosition = holder.adapterPosition
        if (holder is VideoViewHolder) {
            val videoView = holder.videoView
            videoView.stopPlayback()
        }
        if (holder is AudioViewHolder) {
            recycleAudioView(holder)
        }
        if (holder is CheckViewHolder) {
            holder.checkboxLayout.removeAllViews()
        }
        if (holder is RadioViewHolder) {
            holder.radioGroup.removeAllViews()
        }
        if (holder is InputViewHolder) {
            holder.textInputEditText.text = null
            holder.textInputEditText.removeTextChangedListener(textWatchers[adapterPosition])
        }
        if (holder is DropdownViewHolder) {
            holder.autoCompleteTextView.onItemClickListener = null
            dropDownListener.clear()
        }
    }

    private fun recycleAudioView(holder: AudioViewHolder) {
        val adapterPosition = holder.adapterPosition

        val mediaPlayer = mediaPlayers[adapterPosition]

        if (mediaPlayer != null)
            if (mediaPlayer.isPlaying) {
                holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                holder.recordProgress.progress = 0
                mediaPlayer.stop()
                mediaPlayer.release()
                audioHandlers[adapterPosition]?.removeCallbacks(
                    audioHandlersCallback[adapterPosition] ?: Runnable { })
                audioHandlers.remove(adapterPosition)
                audioHandlersCallback.remove(adapterPosition)
                mediaPlayers.remove(adapterPosition)
            }
    }

    fun addRecordFile(recordFile: Uri?, position: Int) {
        if (recordFile == null)
            return
        if (position == -1)
            return
        val audioQuestion = list[position] as AudioQuestion
        audioQuestion.update(recordFile.toFile())
        notifyItemChanged(position)
    }

    fun updatePickedVideo(uri: File) {
        val question = list[lastImageVideoIndex] as VideoQuestion
        question.update(uri)
        notifyItemChanged(lastImageVideoIndex)
    }

    fun updateCameraAndVideoButton() {
        mediaViewHolderIndexes.forEach {
            notifyItemChanged(it)
        }
    }

    private fun titleWithRedAsterisk(
        isRequired: Boolean,
        title: String,
        position: Int
    ): CharSequence {
        return if (!isRequired) {
            val builder = SpannableStringBuilder()
            builder.append(SpannableString("${position + 1}")
                .apply {
                    setSpan(
                        ForegroundColorSpan(Color.BLACK),
                        0,
                        position.toString().length,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        StyleSpan(Typeface.BOLD), 0,
                        position.toString().length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        RelativeSizeSpan(1.2f), 0,
                        position.toString().length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                })
            builder.append(SpannableString(" $title"))


        } else {
            val builder = SpannableStringBuilder()
            builder.append(SpannableString("${position + 1}")
                .apply {
                    setSpan(
                        ForegroundColorSpan(Color.BLACK),
                        0,
                        position.toString().length,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        StyleSpan(Typeface.BOLD), 0,
                        position.toString().length,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        RelativeSizeSpan(1.2f), 0,
                        position.toString().length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                })
            builder.append(SpannableString(" $title"))
            builder.append("  ")
            builder.append(SpannableString("*")
                .apply {
                    setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        1,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        StyleSpan(Typeface.BOLD), 0,
                        1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    setSpan(RelativeSizeSpan(1.1f), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                })
        }
    }


    private fun RecyclerView.smoothSnapToPosition(position: Int) {
        val smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            override fun getHorizontalSnapPreference(): Int = SNAP_TO_START
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

}


