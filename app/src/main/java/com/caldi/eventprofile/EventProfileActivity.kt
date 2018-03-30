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
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.EVENT_ID_KEY
import com.caldi.eventprofile.list.QuestionsAdapter
import com.caldi.eventprofile.list.QuestionsViewModel
import com.caldi.eventprofile.models.EventProfileData
import com.caldi.extensions.hideSoftKeyboard
import com.caldi.factories.EventProfileViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_event_profile.contentViewGroup
import kotlinx.android.synthetic.main.activity_event_profile.createProfilePromptTextView
import kotlinx.android.synthetic.main.activity_event_profile.eventUserNameEditText
import kotlinx.android.synthetic.main.activity_event_profile.loadingPhotoTextView
import kotlinx.android.synthetic.main.activity_event_profile.profilePictureImageView
import kotlinx.android.synthetic.main.activity_event_profile.progressBar
import kotlinx.android.synthetic.main.activity_event_profile.questionsRecyclerView
import kotlinx.android.synthetic.main.activity_event_profile.saveProfileButton
import kotlinx.android.synthetic.main.activity_event_profile.uploadPhotoButton
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class EventProfileActivity : BaseDrawerActivity(), EventProfileView {

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
        setPromptText()

        eventId = intent.getStringExtra(EVENT_ID_KEY)

        eventProfileViewModel = ViewModelProviders.of(this, eventProfileViewModelFactory)[EventProfileViewModel::class.java]
        questionsViewModel = ViewModelProviders.of(this)[QuestionsViewModel::class.java]

        questionsAdapter = QuestionsAdapter(questionsViewModel)
        questionsRecyclerView.layoutManager = LinearLayoutManager(this)
        questionsRecyclerView.adapter = questionsAdapter
        ViewCompat.setNestedScrollingEnabled(questionsRecyclerView, false)

        uploadPhotoButton.setOnClickListener {
            CropImage.activity()
                    .setAspectRatio(1, 1)
                    .start(this)
        }
    }

    override fun onStart() {
        super.onStart()
        setNavigationSelection(R.id.event_profile_item)
        eventProfileViewModel.bind(this)
        if (fetchEventProfile) triggerEventProfileFetchSubject.onNext(eventId)
    }

    override fun onStop() {
        fetchEventProfile = false
        hideSoftKeyboard()
        eventProfileViewModel.unbind()
        super.onStop()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                profilePictureFileSubject.onNext(File(result.uri.path))
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showError(true)
            }
        }
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

    override fun emitInputData(): Observable<EventProfileData> {
        return RxView.clicks(saveProfileButton)
                .map {
                    EventProfileData(eventUserNameEditText.text.toString(), questionsViewModel.getAnswerList(),
                            questionsViewModel.getQuestionList())
                }
                .doOnNext { hideSoftKeyboard() }
    }

    override fun emitProfilePictureFile(): Observable<File> = profilePictureFileSubject

    override fun render(eventProfileViewState: EventProfileViewState) {
        with(eventProfileViewState) {
            if (profilePictureUrl.isNotBlank()) {
                profilePictureImageView.adjustViewBounds = false
                loadingPhotoTextView.visibility = View.VISIBLE
                Picasso.get().load(profilePictureUrl).placeholder(R.drawable.profile_picture_shape)
                        .into(profilePictureImageView, object : Callback {
                            override fun onSuccess() {
                                profilePictureImageView.adjustViewBounds = true
                                loadingPhotoTextView.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                profilePictureImageView.adjustViewBounds = false
                                profilePictureImageView.setImageResource(R.drawable.profile_picture_shape)
                                loadingPhotoTextView.visibility = View.GONE
                            }
                        })
            }
            showProgressBar(progress)
            showError(error, dismissToast)
            eventUserNameEditText.showError(!eventUserNameValid)
            if (eventProfileViewState.renderInputs) {
                questionsAdapter.setQuestionsList(questionViewStateList)
                eventUserNameEditText.setText(eventUserName)
            }

            if (successUpload && !dismissToast) {
                Toast.makeText(this@EventProfileActivity, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
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

    private fun showError(show: Boolean, dismissToast: Boolean = false) {
        if (show && !dismissToast) {
            Toast.makeText(this, getString(R.string.error_event_profile_text), Toast.LENGTH_SHORT).show()
        }
    }
}