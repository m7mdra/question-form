package com.example.questionform

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.questionform.viewholder.RowImageViewHolder
import com.squareup.picasso.Picasso
import java.io.File
import java.net.URI

class ImageAdapter : RecyclerView.Adapter<RowImageViewHolder>() {

    private val list = mutableListOf<String>()

    fun add(url: String) {
        list.add(url)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_row_image, parent, false)
        return RowImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RowImageViewHolder, position: Int) {
        val imageSource: String = list[position]
        Picasso.get().load(imageSource.toUri()).into(holder.selectedImageView)

    }

    override fun getItemCount(): Int {
        return list.size
    }
}