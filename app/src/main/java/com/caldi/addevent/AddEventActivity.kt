package com.caldi.addevent

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.extensions.hideSoftKeyboard
import com.caldi.factories.AddEventViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_add_event.addEventButton
import kotlinx.android.synthetic.main.activity_add_event.contentViewGroup
import kotlinx.android.synthetic.main.activity_add_event.eventCodeEditText
import kotlinx.android.synthetic.main.activity_add_event.eventCodePromptTextView
import kotlinx.android.synthetic.main.activity_add_event.progressBar
import javax.inject.Inject

class AddEventActivity : BaseDrawerActivity(), AddEventView {

    private lateinit var addEventViewModel: AddEventViewModel

    @Inject
    lateinit var addEventViewModelFactory: AddEventViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_add_event)
        super.onCreate(savedInstanceState)
        setNavigationSelection(R.id.events_item)
        setPromptText()

        addEventViewModel = ViewModelProviders.of(this, addEventViewModelFactory)[AddEventViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        addEventViewModel.bind(this)
    }

    override fun onStop() {
        addEventViewModel.unbind()
        super.onStop()
    }

    private fun setPromptText() {
        val promptString = getString(R.string.add_event_prompt)
        val wordToBeColored = getString(R.string.add_event_word_to_be_colored)
        val startOfColoring = promptString.indexOf(wordToBeColored)
        if (startOfColoring > 0) {
            val endOfColoring = startOfColoring + wordToBeColored.length
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

    override fun emitNewEventCode(): Observable<String> {
        return RxView.clicks(addEventButton).map { eventCodeEditText.text.toString() }
    }

    override fun render(addEventViewState: AddEventViewState) {
        hideSoftKeyboard()
        showProgressBar(addEventViewState.inProgress)

        if (addEventViewState.error && !addEventViewState.dismissToast) {
            Toast.makeText(this, getString(R.string.add_event_error), Toast.LENGTH_SHORT).show()
        }

        if (addEventViewState.success) {
            Toast.makeText(this, getString(R.string.new_event_added_text), Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }
}