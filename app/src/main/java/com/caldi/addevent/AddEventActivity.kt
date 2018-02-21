package com.caldi.addevent

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import kotlinx.android.synthetic.main.activity_add_event.contentViewGroup
import kotlinx.android.synthetic.main.activity_add_event.eventCodePromptTextView
import kotlinx.android.synthetic.main.activity_add_event.progressBar

class AddEventActivity : BaseDrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_add_event)
        super.onCreate(savedInstanceState)
        setNavigationSelection(R.id.events_item)
        setPromptText()
    }

    private fun setPromptText() {
        val promptString = getString(R.string.add_event_prompt)
        val startOfColoring = promptString.indexOf(getString(R.string.add_event_word_to_be_colored))
        if (startOfColoring > 0) {
            val endOfColoring = startOfColoring + 4
            val spannableString = SpannableString(promptString)
            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorLightGreen)),
                    startOfColoring, endOfColoring, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            eventCodePromptTextView.setText(spannableString, TextView.BufferType.SPANNABLE)
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            contentViewGroup.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            contentViewGroup.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}