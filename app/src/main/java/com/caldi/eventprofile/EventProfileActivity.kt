package com.caldi.eventprofile

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.EVENT_ID_KEY
import com.caldi.factories.EventProfileViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_event_profile.createProfilePromptTextView
import javax.inject.Inject

class EventProfileActivity : BaseDrawerActivity(), EventProfileView {

    private lateinit var eventProfileViewModel: EventProfileViewModel

    @Inject
    lateinit var eventProfileViewModelFactory: EventProfileViewModelFactory

    companion object {

        fun start(context: Context, eventId: String) {
            val intent = Intent(context, EventProfileActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_event_profile)
        super.onCreate(savedInstanceState)
        setNavigationSelection(R.id.event_profile_item)
        setPromptText()

        val eventId = intent.getStringExtra(EVENT_ID_KEY)
        eventProfileViewModel = ViewModelProviders.of(this, eventProfileViewModelFactory)[EventProfileViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        eventProfileViewModel.bind(this)
    }

    override fun onStop() {
        eventProfileViewModel.unbind()
        super.onStop()
    }

    private fun setPromptText() {
        val promptString = getString(R.string.create_profile_prompt)
        val wordsToBeColored = getString(R.string.create_profile_words_to_be_colored)
        val startOfColoring = promptString.indexOf(wordsToBeColored)
        if (startOfColoring > 0) {
            val endOfColoring = startOfColoring + wordsToBeColored.length
            val spannableString = SpannableString(promptString)
            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorLightGreen)),
                    startOfColoring, endOfColoring, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            createProfilePromptTextView.setText(spannableString, TextView.BufferType.SPANNABLE)
        }
    }

    override fun render(eventProfileState: EventProfileState) {

    }
}