package com.evw.aster

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class Yeview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.gameview, this)
    }
}

