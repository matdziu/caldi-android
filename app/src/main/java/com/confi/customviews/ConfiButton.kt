package com.confi.customviews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.Button

class ConfiButton(context: Context, attrs: AttributeSet? = null) : Button(context, attrs) {

    init {
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
    }
}