package com.confi.customviews

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.confi.R

class ConfiEditText(context: Context, attrs: AttributeSet? = null)
    : ConstraintLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.confi_edit_text, this)
    }
}