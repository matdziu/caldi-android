package com.confi.customviews

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.EditText
import com.confi.R

class ConfiEditText(context: Context, attrs: AttributeSet? = null)
    : EditText(context, attrs) {

    init {
        background = ContextCompat.getDrawable(context, R.drawable.round_background)
        setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText))
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        val paddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
        setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
    }
}