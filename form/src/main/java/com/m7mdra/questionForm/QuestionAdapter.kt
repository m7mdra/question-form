package com.m7mdra.questionForm

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.widget.textChanges
import com.m7mdra.questionForm.question.*
import com.m7mdra.questionForm.question.QuestionType.*
import com.m7mdra.questionForm.viewholder.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit


@Suppress("unused")
class QuestionAdapter(
    private val context: Context,
    private val imagePickListener: ((ImageQuestion, Int) -> Unit)? = null,
    private val audioRecordListener: ((AudioQuestion, Int) -> Unit)? = null,
    private val videoPickListener: ((VideoQuestion, Int) -> Unit)? = null,
    private val imageClickListener: ((Int, Int, String) -> Unit)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = mutableListOf<Question<*>>()

    @SuppressLint("NotifyDataSetChanged")
    fun addQuestions(questions: List<Question<*>>) {
        list.clear()
        list.addAll(questions)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun addQuestion(question: Question<*>) {
        list.add(question)
        notifyItemInserted(list.size - 1)
    }

    fun updateQuestion(position: Int, question: Question<*>) {
        list[position] = question
        notifyItemChanged(position)
    }

    fun updateQuestion(question: Question<*>) {
        val indexOfQuestion = list.indexOf(question)
        if (indexOfQuestion == -1)
            return
        list[indexOfQuestion] = question
        notifyItemChanged(indexOfQuestion)
    }

    private var attachedRecyclerView: RecyclerView? = null
    private var layoutManager: LinearLayoutManager? = null


    private val textWatcherDisposables = SparseArray<Disposable?>()
    private val dropDownListener = SparseArray<AdapterView.OnItemClickListener>()
    private val imageAdapters = SparseArray<ImageAdapter>()
    private val audioPreparedPosition = SparseBooleanArray()
    private val audioViewHolderIndexes = SparseIntArray()
    private val mediaViewHolderIndexes = SparseIntArray()
    private val audioHandlers = SparseArray<Handler>()
    private val audioHandlersCallback = SparseArray<Runnable>()
    private val mediaPlayers = SparseArray<MediaPlayer>()

    fun dispose() {
        mediaPlayers.forEach { _, value ->
            try {
                if (value.isPlaying) {
                    value.stop()
                }

                value.release()
            } catch (error: Exception) {

            }
        }
        audioHandlers.forEach { key, value ->
            val runnable = audioHandlersCallback[key]
            if (runnable != null)
                value.removeCallbacks(runnable)
        }
        dropDownListener.clear()

        textWatcherDisposables.forEach { _, value ->
            if (value != null) {
                if (!value.isDisposed)
                    value.dispose()
            }
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
        holder.titleTextView.text = (list[position] as TitleQuestion).title
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
            titleWithRedAsterisk(question.required, question.title)
        if (question.value != null)
            holder.textInputEditText.setText(question.value)

        val disposable = holder.textInputEditText.textChanges()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { it.isNotEmpty() && it.isNotBlank() }
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribe { question.update(it.toString()) }
        textWatcherDisposables[position] = disposable

        if (question.status.isPendingOrAccepted()) {
            holder.textInputEditText.disable()
        } else {
            holder.textInputEditText.enable()
        }
        holder.stateLayout.inflateViewForStatus(question.status)
    }


    private fun bindDropQuestion(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val holder = viewHolder as DropdownViewHolder
        val question = list[position] as DropdownQuestion
        holder.titleTextView.text =
            titleWithRedAsterisk(question.required, question.title)

        val autoCompleteTextView = holder.autoCompleteTextView

        holder.errorTextView.visibility =
            shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)

        autoCompleteTextView.setText(question.value, false)
        autoCompleteTextView.setAdapter(
            ArrayAdapter(
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
        if (question.status.isPendingOrAccepted()) {
            holder.autoCompleteTextView.disable()
            holder.autoCompleteTextViewLayout.disable()
        } else {
            holder.autoCompleteTextViewLayout.enable()
            holder.autoCompleteTextView.enable()
        }
        holder.stateLayout.inflateViewForStatus(question.status)
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
            titleWithRedAsterisk(question.required, question.title)

        val radioGroup = holder.radioGroup
        val entries = question.entries
        entries.forEach {
            val radioButton = RadioButton(holder.context)
            radioButton.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val radioId = it.hashCode() + position
            radioButton.id = radioId
            radioButton.text = it
            radioButton.isChecked =
                question.value.hashCode() + position == radioId
            radioGroup.addView(radioButton)
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    question.update(it)
                }
            }
        }
        if (question.status.isPendingOrAccepted()) {
            holder.rootView.disable()
            holder.itemView.disable()
            holder.rootView.disableChildren()
        } else {
            holder.rootView.enable()
            holder.itemView.enable()
            holder.rootView.enableChildren()
        }
        holder.stateLayout.inflateViewForStatus(question.status)
    }

    private fun bindAudioQuestion(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val holder = viewHolder as AudioViewHolder
        val question = list[position] as AudioQuestion
        val audio = question.value
        audioViewHolderIndexes.append(position, position)


        holder.errorTextView.visibility = shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)


        holder.titleTextView.text =
            titleWithRedAsterisk(question.required, question.title)
        if (audio != null) {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(audio.path)
            mediaPlayer.prepareAsync()
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
                try {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                        handler.removeCallbacks(runnable)
                        holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    } else {
                        mediaPlayer.start()
                        handler.removeCallbacks(runnable)
                        handler.post(runnable)
                        holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_pause_24)
                    }
                } catch (error: Exception) {
                }
            }

            mediaPlayer.setOnCompletionListener {
                holder.recordProgress.progress = 0
                holder.recordDurationTextView.text = context.getString(R.string.zero_zero)
                holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }
        if (audio == null) {
            holder.playOrStopButton.disable()
        } else {
            holder.playOrStopButton.enable()
        }

        holder.recordAudioButton.text =
            if (isAudioPermissionGranted(holder.context)
            )
                "Record" else "grant permission"
        holder.recordAudioButton.setOnClickListener {

            audioRecordListener?.invoke(question, position)

        }
        if (question.status.isPendingOrAccepted()) {
            holder.recordAudioButton.disable()

        } else {
            holder.recordAudioButton.enable()

        }
        holder.stateLayout.inflateViewForStatus(question.status)


    }

    private fun bindVideoQuestion(
        position: Int,
        viewHolder: RecyclerView.ViewHolder
    ) {
        mediaViewHolderIndexes.append(position, position)
        val holder = viewHolder as VideoViewHolder
        val question = list[position] as VideoQuestion
        val videoView = holder.videoView


        holder.errorTextView.visibility = shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)

        val cameraPermissionGranted = holder.context.isCameraPermissionGranted()
        holder.titleTextView.text =
            titleWithRedAsterisk(question.mandatory, question.title)
        holder.captureOrPickVideoButton.text =
            if (cameraPermissionGranted) "Record video" else "Grant permission"
        holder.captureOrPickVideoButton.setOnClickListener {

            videoPickListener?.invoke(question, position)
        }

        val video = question.value
        if (video != null && video.isNotEmpty()) {
            videoView.show()
            videoView.setVideoURI(Uri.parse(video))

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
        if (question.status.isPendingOrAccepted()) {
            holder.captureOrPickVideoButton.disable()
        } else {
            holder.captureOrPickVideoButton.enable()
        }
        holder.stateLayout.inflateViewForStatus(question.status)

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
            titleWithRedAsterisk(question.mandatory, question.title)
        val imageAdapter = ImageAdapter(context) { childPosition, image ->
            imageClickListener?.invoke(position, childPosition, image)
        }
        holder.imageButton.setOnClickListener {

            imagePickListener?.invoke(question, position)
        }
        imageAdapters[adapterPosition] = imageAdapter
        if (question.value.isNotEmpty()) {
            imageAdapter.addAll(question.value)
        }
        holder.imagesRecyclerView.adapter = imageAdapter
        if (question.status.isPendingOrAccepted()) {
            holder.imageButton.disable()
        } else {
            holder.imageButton.enable()
        }
        holder.stateLayout.inflateViewForStatus(question.status)

    }


    private fun bindCheckQuestion(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val holder = viewHolder as CheckViewHolder
        val question = list[position] as CheckQuestion
        holder.titleTextView.text =
            titleWithRedAsterisk(question.required, question.title)

        holder.errorTextView.visibility = shouldShowError(question.hasError)
        holder.itemView.setBackgroundResource(if (question.hasError) R.drawable.error_stroke else 0)
        question.entries.forEachIndexed { index, s ->
            val checkBox = CheckBox(holder.context)
            checkBox.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            checkBox.isChecked = question.selectionMap.containsKey(index)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    question.selectionMap[index] = s
                } else {
                    question.selectionMap.remove(index)
                }
                question.update(listOf())
            }
            checkBox.text = s
            checkBox.id = s.hashCode()
            holder.checkboxLayout.addView(checkBox)
        }

        if (question.status.isPendingOrAccepted()) {
            holder.rootView.disable()
            holder.itemView.disable()
            holder.rootView.disableChildren()
        } else {
            holder.rootView.enable()
            holder.itemView.enable()
            holder.rootView.enableChildren()
        }

        holder.stateLayout.inflateViewForStatus(question.status)
    }

    private fun FrameLayout.inflateViewForStatus(status: QuestionStatus) {
        val layoutId = when (status) {
            QuestionStatus.Accepted -> R.layout.layout_accepted_status
            QuestionStatus.Pending -> R.layout.layout_pending_status
            QuestionStatus.Rejected -> R.layout.layout_rejected_status
            QuestionStatus.Default -> R.layout.layout_default_status
        }
        val view = layoutInflater.inflate(layoutId, null, false)
        removeAllViews()
        addView(view)
    }

    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    private fun shouldShowError(predicate: Boolean) =
        if (predicate) View.VISIBLE else View.GONE

    private fun isAudioPermissionGranted(context: Context) =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED


    override fun getItemCount(): Int {
        return list.size
    }

    fun validate(): Boolean {

        notifyErrors()

        return list.all { it.isValid() }
    }

    private var previousErrorIndex = -1

    private fun notifyErrors() {
        post {
            val first = list.firstOrNull { !it.validate() } ?: return@post
            val indexOfFirstError = list.indexOf(first)

            "${first.identifier}/${first.questionType}: ${first.status} valid:${first.isValid()}".log()
            val visibleRange = layoutManager.visibleRange()
            "previousErrorIndex:${previousErrorIndex} is in visible range $visibleRange ? ${previousErrorIndex in visibleRange} ".log()
            if (indexOfFirstError != -1) {
                if (previousErrorIndex != -1 &&
                    previousErrorIndex != indexOfFirstError
                ) {
                    notifyItemChanged(previousErrorIndex)
                }
                attachedRecyclerView?.smoothSnapToPosition(indexOfFirstError)
                previousErrorIndex = indexOfFirstError
                notifyItemChanged(indexOfFirstError)
            }
        }


    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
        layoutManager = recyclerView.layoutManager as? LinearLayoutManager
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        attachedRecyclerView = null
        layoutManager = null
    }


    private fun post(block: () -> Unit) {
        attachedRecyclerView?.post(block)

    }


    fun collect(): List<Question<*>> {
        return list.filter { it !is TitleQuestion }
            .filter { it.status.isNotPendingNorAccepted() }
            .map { it }
    }

    fun updateRecordAudioButtons() {
        audioViewHolderIndexes.forEach { key, _ ->
            notifyItemChanged(key)
        }
    }

    private fun LinearLayoutManager?.visibleRange(): IntRange {
        val firstVisibleItemPosition = this?.findFirstVisibleItemPosition() ?: 0
        val lastVisibleItemPosition = this?.findLastVisibleItemPosition() ?: 0
        return IntRange(firstVisibleItemPosition, lastVisibleItemPosition)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val adapterPosition = holder.adapterPosition
        if (holder is VideoViewHolder) {
            val videoView = holder.videoView
            holder.playOrStopButton.gone()
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
            val disposable = textWatcherDisposables[adapterPosition]
            if (disposable != null) {
                if (!disposable.isDisposed)
                    disposable.dispose()
            }
        }
        if (holder is DropdownViewHolder) {
            holder.autoCompleteTextView.onItemClickListener = null
            dropDownListener.remove(holder.adapterPosition)
        }
    }

    private fun recycleAudioView(holder: AudioViewHolder) {
        val adapterPosition = holder.adapterPosition

        val mediaPlayer = mediaPlayers[adapterPosition]
        holder.recordProgress.progress = 0
        holder.recordDurationTextView.text = context.getString(R.string.zero_zero)
        holder.playOrStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        audioPreparedPosition[adapterPosition] = false
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

    @SuppressLint("NotifyDataSetChanged")
    fun addRecordFile(recordFile: File?, position: Int) {
        if (recordFile == null)
            return
        if (position == -1)
            return
        recordFile.log()
        position.log()
        val audioQuestion = list[position] as AudioQuestion
        audioQuestion.update(recordFile)
        //TODO: for some reasons the audio question dose not
        // rebuild when called RecyclerView.Adapter#notifyItemChanged
        // as a last resort notifyDataSetChanged is used
        notifyDataSetChanged()
    }

    fun updatePickedVideo(uri: String, position: Int) {
        if (position == -1) return
        val question = list[position] as VideoQuestion
        question.update(uri)
        notifyItemChanged(position)
    }


    fun updateImageAdapterAtPosition(uri: String, position: Int) {
        if (position == -1) return
        val adapter: ImageAdapter = imageAdapters[position] ?: return
        val imageQuestion = list[position] as ImageQuestion
        imageQuestion.update(mutableListOf(uri))
        adapter.add(uri)
        notifyItemChanged(position)
    }

    fun updateCameraAndVideoButton() {
        mediaViewHolderIndexes.forEach { _, value ->
            notifyItemChanged(value)
        }
    }

    private fun titleWithRedAsterisk(
        isRequired: Boolean,
        title: String
    ): CharSequence {
        return if (!isRequired) {
            val builder = SpannableStringBuilder()
//            builder.append(SpannableString("${position + 1}").boldAndColor(Color.BLACK))
            builder.append(SpannableString(" $title"))
        } else {
            val builder = SpannableStringBuilder()
//            builder.append(SpannableString("${position + 1}").boldAndColor(Color.BLACK))
            builder.append(SpannableString(title))
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


