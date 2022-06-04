package com.example.questionform

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemoryCacheAdapter
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.m7mdra.questionForm.log
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

@GlideModule
class MyGlideApp : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, 1024 * 1024 * 1024))
        builder.addGlobalRequestListener(object : RequestListener<Any> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Any>?,
                isFirstResource: Boolean
            ): Boolean {
                "onLoadFailed".log()
                return false
            }

            override fun onResourceReady(
                resource: Any?,
                model: Any?,
                target: Target<Any>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                "onResourceReady:$resource $model $target $dataSource $isFirstResource".log()
                return false
            }

        })
        builder.setMemoryCache(LruResourceCache(100 * 1024 * 1024))
        builder.setDefaultRequestOptions {
             RequestOptions()
                 .transform(RoundedCornersTransformation(16,8,RoundedCornersTransformation.CornerType.ALL))
                 .diskCacheStrategy(DiskCacheStrategy.ALL)
        }


    }
}