package com.m7mdra.questionForm

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.recyclerview.widget.RecyclerView
import com.m7mdra.questionForm.viewholder.RowImageViewHolder
import com.squareup.picasso.Cache
import com.squareup.picasso.LruCache
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
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

    fun removeAt(position:Int){
        list.removeAt(position)
        notifyItemRemoved(position)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_row_image, parent, false)
        return RowImageViewHolder(view)
    }

    private val picasso: Picasso by lazy {
        Picasso.Builder(context)
            .memoryCache(LruCache(context))
            .loggingEnabled(BuildConfig.DEBUG)
            .downloader(OkHttp3Downloader(context))
            .build()
    }


    override fun onBindViewHolder(holder: RowImageViewHolder, position: Int) {
        val imageSource: String = list[position]
        holder.view.setOnClickListener {
            clickListener.invoke(position, imageSource)
        }
        if(imageSource.isNotEmpty()) {
            if (URLUtil.isHttpUrl(imageSource) || URLUtil.isHttpsUrl(imageSource)) {
                picasso.load(imageSource)
                    .fit()
                    .centerInside()
                    .error(R.drawable.placeholder_image)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.selectedImageView)
            } else  {
                picasso
                    .load(File(imageSource))
                    .fit()
                    .error(R.drawable.placeholder_image)
                    .placeholder(R.drawable.placeholder_image)
                    .centerInside()
                    .into(holder.selectedImageView)
            }
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
}
