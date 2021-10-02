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
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.core.view.allViews
import androidx.core.view.children
import androidx.core.view.descendants
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
    private val imagePickListener: ((ImageQuestion, Int) -> Unit)? = null,
    private val audioRecordListener: ((AudioQuestion, Int) -> Unit)? = null,
    private val videoPickListener: ((VideoQuestion, Int) -> Unit)? = null,
    private val imageClickListener: ((Int, Int, String) -> Unit)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var attachedRecyclerView: RecyclerView? = null

    private var lastImagePickIndex = -1
    private var lastImageVideoIndex = -1

    private val textWatchers = SparseArray<TextInputEditTextWatcher>()
    private val dropDownListener = SparseArray<AdapterView.OnItemClickListener>()
    private val imageAdapters = SparseArray<ImageAdapter>()

    private val audioViewHolderIndexes = SparseIntArray()
    private val mediaViewHolderIndexes = SparseIntArray()
    private val audioHandlers = SparseArray<Handler>()
    private val audioHandlersCallback = SparseArray<Runnable>()
    private val mediaPlayers = SparseArray<MediaPlayer>()

    fun clear() {
        mediaPlayers.forEach { _, value ->
            if (value.isPlaying) {
                value.stop()
            }

            value.release()
        }
        audioHandlers.forEach { key, value ->
            val runnable = audioHandlersCallback[key]
            if (runnable != null)
                value.removeCallbacks(runnable)
        }
        dropDownListener.clear()

        textWatchers.forEach { _, value ->
            value.removeWatcher()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return viewHolder(viewType, parent, layoutInflater)
    }


    override fun getItemViewType(position: Int): Int {
        return list[position].questionType.ordinal
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Title.ordinal -> {
                bindTitleQuestion(viewHolder, position)
            }
            Input.ordinal -> {
                bindInputQuestion(position, viewHolder)
            }
            Dropdown.ordinal -> {
                bindDropQuestion(viewHolder, position)
            }
            Radio.ordinal -> {
                bindRadioQuestion(viewHolder, position)
            }
            Check.ordinal -> {
                bindCheckQuestion(viewHolder, position)
            }
            Audio.ordinal -> {
                bindAudioQuestion(viewHolder, position)
            }
            Image.ordinal -> {
                bindImageQuestion(position, viewHolder)
            }
            Video.ordinal -> {
                bindVideoQuestion(position, viewHolder)
            }

        }
    }

    private fun bindTitleQuestion(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val holder = viewHolder as TitleViewHolder
        holder.titleTextView.text = list[position].title
    }

    private fun bindInputQuestion(
        position: Int,
        viewHolder: RecyclerView.ViewHolder
    ) {
        val question = list[position] as InputQuestion

        val holder = viewHolder as InputViewHolder
        holder.errorTextView.visibility = shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)

        holder.titleTextView.text =
            titleWithRedAsterisk(question.required, question.title, position)
        if (question.value != null)
            holder.textInputEditText.setText(question.value)
        val textWatcher =
            object : TextInputEditTextWatcher(holder.textInputEditText) {
                override fun afterTextChanged(s: Editable) {
                    if (s.isNotEmpty())
                        question.update(s.toString())
                }
            }
        textWatchers[position] = textWatcher

        holder.textInputEditText.addTextChangedListener(textWatcher)
        if (question.done) {
            holder.rootView.disable()
            holder.itemView.disable()
            holder.rootView.disableChildren()
            holder.submittedTextView.show()
        } else {
            holder.rootView.enable()
            holder.itemView.enable()
            holder.rootView.enableChildren()
            holder.submittedTextView.gone()
        }

    }

    private fun bindDropQuestion(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val holder = viewHolder as DropdownViewHolder
        val question = list[position] as DropdownQuestion
        holder.titleTextView.text =
            titleWithRedAsterisk(question.required, question.title, position)

        val autoCompleteTextView = holder.autoCompleteTextView

        holder.errorTextView.visibility =
            shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)

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
        if (question.done) {
            holder.rootView.disable()
            holder.itemView.disable()
            holder.rootView.disableChildren()
            holder.submittedTextView.show()
        } else {
            holder.rootView.enable()
            holder.itemView.enable()
            holder.rootView.enableChildren()
            holder.submittedTextView.gone()
        }
    }

    private fun bindRadioQuestion(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val holder = viewHolder as RadioViewHolder
        val question = list[position] as RadioQuestion

        holder.errorTextView.visibility = shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)

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
                if (isChecked) {
                    question.update(it)
                }
            }
        }
        if (question.done) {
            holder.rootView.disable()
            holder.itemView.disable()
            holder.rootView.disableChildren()
            holder.submittedTextView.show()
        } else {
            holder.rootView.enable()
            holder.itemView.enable()
            holder.rootView.enableChildren()
            holder.submittedTextView.gone()
        }
    }

    private fun bindVideoQuestion(
        position: Int,
        viewHolder: RecyclerView.ViewHolder
    ) {
        mediaViewHolderIndexes.append(position, position)
        val holder = viewHolder as VideoViewHolder
        val question = list[position] as VideoQuestion
        question.log()
        val videoView = holder.videoView


        holder.errorTextView.visibility = shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)

        val cameraPermissionGranted = holder.context.isCameraPermissionGranted()
        holder.titleTextView.text =
            titleWithRedAsterisk(question.mandatory, question.title, position)
        holder.captureOrPickVideoButton.text =
            if (cameraPermissionGranted) "Record video" else "Grant permission"
        holder.captureOrPickVideoButton.setOnClickListener {
            lastImageVideoIndex = position
            videoPickListener?.invoke(question, position)
        }

        val file = question.value
        if (file != null) {
            videoView.show()
            videoView.setVideoURI(file.toUri())
            videoView.setOnPreparedListener {
                holder.playOrStopButton.show()
                holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
            }
            videoView.setOnCompletionListener {
                holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)

            }

        } else {
            holder.playOrStopButton.gone()
            videoView.gone()
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
        if (question.done) {
            holder.rootView.disable()
            holder.itemView.disable()
            holder.rootView.disableChildren()
            holder.submittedTextView.show()
        } else {
            holder.rootView.enable()
            holder.itemView.enable()
            holder.rootView.enableChildren()
            holder.submittedTextView.gone()
        }

    }

    private fun bindImageQuestion(
        position: Int,
        viewHolder: RecyclerView.ViewHolder
    ) {
        mediaViewHolderIndexes.append(position, position)

        val holder = viewHolder as ImageViewHolder
        val question = list[position] as ImageQuestion
        val adapterPosition = holder.adapterPosition
        val cameraPermissionGranted = holder.context.isCameraPermissionGranted()


        holder.errorTextView.visibility = shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)


        holder.imageButton.text =
            if (cameraPermissionGranted) "Capture image" else "Grant permission"


        holder.titleTextView.text =
            titleWithRedAsterisk(question.mandatory, question.title, position)
        val imageAdapter = ImageAdapter { childPosition, image ->
            imageClickListener?.invoke(position, childPosition, image)
        }
        holder.imageButton.setOnClickListener {
            lastImagePickIndex = position
            imagePickListener?.invoke(question, position)
        }
        imageAdapters[adapterPosition] = imageAdapter
        if (question.value.isNotEmpty())
            imageAdapter.addAll(question.value)
        holder.imagesRecyclerView.adapter = imageAdapter
        if (question.done) {
            holder.rootView.disable()
            holder.itemView.disable()
            holder.rootView.disableChildren()
            holder.submittedTextView.show()
        } else {
            holder.rootView.enable()
            holder.itemView.enable()
            holder.rootView.enableChildren()
            holder.submittedTextView.gone()
        }

    }

    private fun bindAudioQuestion(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val holder = viewHolder as AudioViewHolder
        val question = list[position] as AudioQuestion
        audioViewHolderIndexes.append(position, position)


        holder.errorTextView.visibility = shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)


        holder.titleTextView.text =
            titleWithRedAsterisk(question.required, question.title, position)
        if (question.value != null) {
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
                            holder.recordDurationTextView.text =
                                (playPosition / 1000L).formatDuration()
                        }
                        handler.postDelayed(this, 1000 - playPosition.toLong() % 1000)
                    }
                }

            }
            mediaPlayers.append(position, mediaPlayer)
            audioHandlers[position] = handler
            audioHandlersCallback[position] = runnable
            holder.playOrStopButton.setOnClickListener {
                val audio = question.value ?: return@setOnClickListener
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    holder.recordDurationTextView.text =
                        context.getString(R.string.zero_zero)
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
                holder.recordDurationTextView.text = context.getString(R.string.zero_zero)
                holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

            }
        }
        holder.playOrStopButton.isEnabled = question.value != null

        holder.recordAudioButton.text =
            if (isAudioPermissionGranted(holder.context)
            )
                "Record" else "grant permission"
        holder.recordAudioButton.setOnClickListener {

            audioRecordListener?.invoke(question, position)

        }
        if (question.done) {
            holder.rootView.disable()
            holder.itemView.disable()
            holder.rootView.disableChildren()
            holder.submittedTextView.show()
        } else {
            holder.rootView.enable()
            holder.itemView.enable()
            holder.rootView.enableChildren()
            holder.submittedTextView.gone()
        }

    }

    private fun bindCheckQuestion(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val holder = viewHolder as CheckViewHolder
        val question = list[position] as CheckQuestion
        holder.titleTextView.text =
            titleWithRedAsterisk(question.required, question.title, position)

        holder.errorTextView.visibility = shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)




        question.entries.forEachIndexed { index, s ->
            val checkBox = CheckBox(holder.context)
            checkBox.isChecked = question.selectionMap.containsKey(index)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
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
        if (question.done) {
            holder.rootView.disable()
            holder.itemView.disable()
            holder.rootView.disableChildren()
            holder.submittedTextView.show()
        } else {
            holder.rootView.enable()
            holder.itemView.enable()

            holder.rootView.enableChildren()
            holder.submittedTextView.gone()
        }
    }

    private fun shouldShowError(predicate: Boolean) =
        if (predicate) View.VISIBLE else View.GONE

    private fun isAudioPermissionGranted(context: Context) =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

    fun updateImageAdapterAtPosition(uri: String) {

        val adapter: ImageAdapter = imageAdapters[lastImagePickIndex] ?: return
        val imageQuestion = list[lastImagePickIndex] as ImageQuestion
        imageQuestion.update(mutableListOf(uri))
        adapter.add(uri)
        notifyItemChanged(lastImagePickIndex)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun validate(): Boolean {

        notifyErrors()

        return list.all { it.isValid() }
    }

    private fun notifyErrors() {
        post {
            val first = list.firstOrNull { !it.validate() } ?: return@post
            val indexOfFirstError = list.indexOf(first)

            if (indexOfFirstError != -1) {
                attachedRecyclerView?.smoothSnapToPosition(indexOfFirstError)
                notifyItemChanged(indexOfFirstError)
            }
            list.filter { !it.isValid() }.forEachIndexed { index, _ ->
                notifyItemChanged(index)
            }
        }


    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        attachedRecyclerView = null
    }


    private fun post(block: () -> Unit) {
        attachedRecyclerView?.post(block)

    }


    fun collect(): List<Question<*>> {
        return list.filter { it !is TitleQuestion }
            .map { it }
    }

    fun updateRecordAudioButtons() {
        audioViewHolderIndexes.forEach { key, _ ->
            notifyItemChanged(key)
        }
    }

    private fun LinearLayoutManager.visibleRange(): IntRange {
        val firstVisibleItemPosition = findFirstVisibleItemPosition()
        val lastVisibleItemPosition = findLastVisibleItemPosition()
        return IntRange(firstVisibleItemPosition, lastVisibleItemPosition)
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
            dropDownListener.remove(holder.adapterPosition)
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
            } else {
                mediaPlayer.release()
            }
        audioHandlers[adapterPosition]?.removeCallbacks(
            audioHandlersCallback[adapterPosition] ?: Runnable { })
        audioHandlers.remove(adapterPosition)
        audioHandlersCallback.remove(adapterPosition)
        mediaPlayers.remove(adapterPosition)
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
        mediaViewHolderIndexes.forEach { _, value ->
            notifyItemChanged(value)
        }
    }

    private fun titleWithRedAsterisk(
        isRequired: Boolean,
        title: String,
        position: Int
    ): CharSequence {
        return if (!isRequired) {
            val builder = SpannableStringBuilder()
//            builder.append(SpannableString("${position + 1}").boldAndColor(Color.BLACK))
            builder.append(SpannableString(" $title"))
        } else {
            val builder = SpannableStringBuilder()
//            builder.append(SpannableString("${position + 1}").boldAndColor(Color.BLACK))
            builder.append(SpannableString("$title"))
            builder.append("  ")
            builder.append(SpannableString("*").boldAndColor(Color.RED))
        }
    }

    private fun SpannableString.boldAndColor(color: Int): SpannableString {
        setSpan(
            ForegroundColorSpan(color),
            0,
            toString().length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        setSpan(
            StyleSpan(Typeface.BOLD), 0,
            toString().length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        setSpan(RelativeSizeSpan(1.1f), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return this
    }

    private fun RecyclerView.smoothSnapToPosition(position: Int) {
        val smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            override fun getHorizontalSnapPreference(): Int = SNAP_TO_START
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun viewHolder(
        viewType: Int,
        parent: ViewGroup,
        layoutInflater: LayoutInflater
    ) = when (viewType) {
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


