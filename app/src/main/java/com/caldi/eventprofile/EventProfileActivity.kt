package com.caldi.eventprofile

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.common.models.EventProfileData
import com.caldi.constants.EVENT_ID_KEY
import com.caldi.eventprofile.list.QuestionsAdapter
import com.caldi.extensions.hideSoftKeyboard
import com.caldi.factories.EventProfileViewModelFactory
import com.caldi.injection.modules.GlideApp
import com.caldi.onboarding.OnboardingInfo
import com.caldi.people.meetpeople.MeetPeopleActivity
import com.jakewharton.rxbinding2.view.RxView
import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
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
import kotlinx.android.synthetic.main.toolbar.logo
import java.io.File
import javax.inject.Inject

class EventProfileActivity : BaseDrawerActivity(), EventProfileView {

    private lateinit var eventProfileViewModel: EventProfileViewModel

    private val questionsAdapter = QuestionsAdapter()

    private var fetchEventProfile = true
    private lateinit var triggerEventProfileFetchSubject: Subject<String>
    private lateinit var profilePictureFileSubject: Subject<File>

    private var currentProfilePictureUrl = ""

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

        eventProfileViewModel = ViewModelProviders.of(this, eventProfileViewModelFactory)[EventProfileViewModel::class.java]

        questionsRecyclerView.layoutManager = LinearLayoutManager(this)
        questionsRecyclerView.adapter = questionsAdapter
        ViewCompat.setNestedScrollingEnabled(questionsRecyclerView, false)

        uploadPhotoButton.setOnClickListener { askForImage() }
        profilePictureImageView.setOnClickListener { askForImage() }

        showOnboarding(
                OnboardingInfo(logo, getString(R.string.onboarding_event_profile), "eventProfileOnboarding")
        )
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
        initEmitters()
        eventProfileViewModel.bind(this)
        if (fetchEventProfile) {
            triggerEventProfileFetchSubject.onNext(eventId)
            fetchEventProfile = false
        }
    }

    private fun initEmitters() {
        profilePictureFileSubject = PublishSubject.create()
        triggerEventProfileFetchSubject = PublishSubject.create()
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
                            userLinkUrl = userLinkEditText.text.toString(),
                            answers = questionsAdapter.answers,
                            profilePicture = currentProfilePictureUrl)
                }
                .doOnNext { hideSoftKeyboard() }
    }

    override fun emitProfilePictureFile(): Observable<File> = profilePictureFileSubject

    override fun render(eventProfileViewState: EventProfileViewState) {
        with(eventProfileViewState) {
            if (profilePictureUrl.isNotBlank()) {
                currentProfilePictureUrl = profilePictureUrl
                loadProfilePicture(profilePictureUrl)
            }
            showProgressBar(progress)
            showError(error, dismissToast)
            eventUserNameEditText.showError(!eventUserNameValid)
            if (eventProfileViewState.renderInputs) {
                questionsAdapter.setQuestionViewStates(questionViewStates)
                eventUserNameEditText.setText(eventUserName)
                userLinkEditText.setText(userLinkUrl)
            }

            if (updateSuccess && !dismissToast) {
                Toast.makeText(this@EventProfileActivity, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                MeetPeopleActivity.start(this@EventProfileActivity, eventId)
            }
            eventUserNameEditText.clearFocus()
        }
    }

    private fun loadProfilePicture(profilePictureUrl: String) {
        profilePictureImageView.adjustViewBounds = false
        loadingPhotoTextView.visibility = View.VISIBLE
        GlideApp.with(this)
                .load(profilePictureUrl)
                .placeholder(R.drawable.profile_picture_shape)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable,
                                                 model: Any,
                                                 target: Target<Drawable>,
                                                 dataSource: DataSource,
                                                 isFirstResource: Boolean): Boolean {
                        profilePictureImageView.adjustViewBounds = true
                        loadingPhotoTextView.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?,
                                              model: Any,
                                              target: Target<Drawable>,
                                              isFirstResource: Boolean): Boolean {
                        profilePictureImageView.adjustViewBounds = false
                        profilePictureImageView.setImageResource(R.drawable.profile_picture_shape)
                        loadingPhotoTextView.visibility = View.GONE
                        return false
                    }
                })
                .into(profilePictureImageView)
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