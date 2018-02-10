package com.caldi.customviews

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.EditText
import com.caldi.R

class CaldiEditText(context: Context, attrs: AttributeSet? = null) : EditText(context, attrs) {

    init {
        background = ContextCompat.getDrawable(context, R.drawable.round_empty_green_background)
        setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText))
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        val paddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
        setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
    }
}