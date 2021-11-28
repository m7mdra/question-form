package com.m7mdra.questionForm

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import java.util.jar.Attributes

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class InflatableLayout @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attributes, defStyle, defStyleRes) {
    private var layoutResId: Int

    init {
        context.theme.obtainStyledAttributes(
            attributes,
            R.styleable.InflatableLayout,
            0, 0
        ).apply {

            try {
                layoutResId = getResourceId(R.styleable.InflatableLayout_layout, -1)
            } finally {
                recycle()
            }
        }
        initView()
    }
    private fun initView(){
        removeAllViews()
        inflate(context,layoutResId,this)
    }
    fun inflateNewView(@LayoutRes layoutId:Int){
        this.layoutResId = layoutId
        initView()
    }


}