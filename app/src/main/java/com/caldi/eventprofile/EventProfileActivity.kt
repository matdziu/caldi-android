package com.caldi.eventprofile

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.EVENT_ID_KEY
import com.caldi.eventprofile.list.QuestionsAdapter
import com.caldi.eventprofile.list.QuestionsViewModel
import com.caldi.eventprofile.models.EventProfileData
import com.caldi.extensions.hideSoftKeyboard
import com.caldi.factories.EventProfileViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_event_profile.contentViewGroup
import kotlinx.android.synthetic.main.activity_event_profile.createProfilePromptTextView
import kotlinx.android.synthetic.main.activity_event_profile.eventUserNameEditText
import kotlinx.android.synthetic.main.activity_event_profile.profilePictureImageView
import kotlinx.android.synthetic.main.activity_event_profile.progressBar
import kotlinx.android.synthetic.main.activity_event_profile.questionsRecyclerView
import kotlinx.android.synthetic.main.activity_event_profile.saveProfileButton
import java.io.File
import javax.inject.Inject

class EventProfileActivity : BaseDrawerActivity(), EventProfileView {

    private var eventId = ""

    private lateinit var eventProfileViewModel: EventProfileViewModel
    private lateinit var questionsViewModel: QuestionsViewModel

    private lateinit var questionsAdapter: QuestionsAdapter

    private var fetchEventProfile = true
    private val triggerEventProfileFetchSubject = PublishSubject.create<String>()
    private val profilePictureFileSubject = PublishSubject.create<File>()

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

        eventId = intent.getStringExtra(EVENT_ID_KEY)

        eventProfileViewModel = ViewModelProviders.of(this, eventProfileViewModelFactory)[EventProfileViewModel::class.java]
        questionsViewModel = ViewModelProviders.of(this)[QuestionsViewModel::class.java]

        questionsAdapter = QuestionsAdapter(questionsViewModel)
        questionsRecyclerView.layoutManager = LinearLayoutManager(this)
        questionsRecyclerView.adapter = questionsAdapter
        ViewCompat.setNestedScrollingEnabled(questionsRecyclerView, false)
    }

    override fun onStart() {
        super.onStart()
        eventProfileViewModel.bind(this)
        if (fetchEventProfile) triggerEventProfileFetchSubject.onNext(eventId)
    }

    override fun onStop() {
        fetchEventProfile = false
        hideSoftKeyboard()
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

    override fun emitEventProfileFetchingTrigger(): Observable<String> = triggerEventProfileFetchSubject

    override fun emitInputData(): Observable<Pair<String, EventProfileData>> {
        return RxView.clicks(saveProfileButton).map {
            Pair(eventId, EventProfileData(eventUserNameEditText.text.toString(), questionsViewModel.getAnswerList(),
                    questionsViewModel.getQuestionList()))
        }
    }

    override fun emitProfilePictureFile(): Observable<File> = profilePictureFileSubject

    override fun render(eventProfileViewState: EventProfileViewState) {
        with(eventProfileViewState) {
            profilePictureUrl?.let { Glide.with(this@EventProfileActivity).load(it).into(profilePictureImageView) }
            showProgressBar(progress)
            showError(error, dismissToast)
            eventUserNameEditText.showError(!eventUserNameValid)
            questionsAdapter.setQuestionsList(questionViewStateList)
            if (eventProfileViewState.renderEventName) eventUserNameEditText.setText(eventUserName)

            if (successUpload && !dismissToast) {
                Toast.makeText(this@EventProfileActivity, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                finish()
            }
            eventUserNameEditText.clearFocus()
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

    private fun showError(show: Boolean, dismissToast: Boolean) {
        if (show && !dismissToast) {
            Toast.makeText(this, getString(R.string.error_event_profile_text), Toast.LENGTH_SHORT).show()
        }
    }
}