package com.caldi.eventprofile

import android.arch.lifecycle.ViewModelProviders
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
import com.caldi.eventprofile.list.QuestionsAdapter
import com.caldi.eventprofile.list.QuestionsViewModel
import com.caldi.eventprofile.models.EventProfileData
import com.caldi.extensions.hideSoftKeyboard
import com.caldi.factories.EventProfileViewModelFactory
import com.caldi.meetpeople.MeetPeopleActivity
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
import kotlinx.android.synthetic.main.activity_event_profile.userLinkEditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_event_profile)
        super.onCreate(savedInstanceState)
        setPromptText()

        eventProfileViewModel = ViewModelProviders.of(this, eventProfileViewModelFactory)[EventProfileViewModel::class.java]
        questionsViewModel = ViewModelProviders.of(this)[QuestionsViewModel::class.java]

        questionsAdapter = QuestionsAdapter(questionsViewModel)
        questionsRecyclerView.layoutManager = LinearLayoutManager(this)
        questionsRecyclerView.adapter = questionsAdapter
        ViewCompat.setNestedScrollingEnabled(questionsRecyclerView, false)

        uploadPhotoButton.setOnClickListener { askForImage() }
        profilePictureImageView.setOnClickListener { askForImage() }
    }

    private fun askForImage() {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this)
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.event_profile_item)
    }

    override fun onStart() {
        super.onStart()
        eventProfileViewModel.bind(this)
        if (fetchEventProfile) {
            triggerEventProfileFetchSubject.onNext(eventId)
            fetchEventProfile = false
        }
    }

    override fun onStop() {
        eventProfileViewModel.unbind()
        hideSoftKeyboard()
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

    override fun emitEventProfileData(): Observable<EventProfileData> {
        return RxView.clicks(saveProfileButton)
                .map {
                    EventProfileData(
                            eventUserName = eventUserNameEditText.text.toString(),
                            answerList = questionsViewModel.getAnswerList(),
                            questionList = questionsViewModel.getQuestionList(),
                            userLinkUrl = userLinkEditText.text.toString())
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

            if (updateSuccess && !dismissToast) {
                Toast.makeText(this@EventProfileActivity, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@EventProfileActivity, MeetPeopleActivity::class.java))
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