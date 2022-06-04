package com.m7mdra.questionForm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.m7mdra.questionForm.viewholder.RowImageViewHolder
import java.io.File

class ImageAdapter(
    private val context: Context,
    private val clickListener: (Int, String) -> Unit = { _, _ -> }
) :
    RecyclerView.Adapter<RowImageViewHolder>() {

    private val list = mutableListOf<String>()

    fun add(url: String) {
        list.add(url)
        notifyItemInserted(list.size - 1)
    }

    fun addAll(urls: List<String>) {
        list.addAll(urls)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_row_image, parent, false)
        return RowImageViewHolder(view)
    }

    private val requestManager by lazy {
        Glide.with(context)
    }


    override fun onBindViewHolder(holder: RowImageViewHolder, position: Int) {
        val imageSource: String = list[position]
        holder.view.setOnClickListener {
            clickListener.invoke(position, imageSource)
        }
        if (imageSource.isNotEmpty()) {
            val request = if (URLUtil.isHttpUrl(imageSource) || URLUtil.isHttpsUrl(imageSource)) {
                requestManager.load(imageSource)

            } else {
                requestManager.load(File(imageSource))

            }
            request

                .centerInside()
                .error(R.drawable.placeholder_image)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.selectedImageView)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
}
