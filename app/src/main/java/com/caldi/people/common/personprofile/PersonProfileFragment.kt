package com.caldi.people.common.personprofile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.caldi.R
import com.caldi.common.states.PersonProfileViewState
import com.caldi.constants.PERSON_PROFILE_VIEW_STATE_KEY
import com.caldi.injection.modules.GlideApp
import com.caldi.people.common.PeopleActivity
import com.caldi.people.meetpeople.list.AnswersAdapter
import kotlinx.android.synthetic.main.fragment_person_profile.acceptProfileButton
import kotlinx.android.synthetic.main.fragment_person_profile.answersRecyclerView
import kotlinx.android.synthetic.main.fragment_person_profile.dismissProfileButton
import kotlinx.android.synthetic.main.fragment_person_profile.divider
import kotlinx.android.synthetic.main.fragment_person_profile.eventUserNameTextView
import kotlinx.android.synthetic.main.fragment_person_profile.loadingPhotoTextView
import kotlinx.android.synthetic.main.fragment_person_profile.profilePictureImageView
import kotlinx.android.synthetic.main.fragment_person_profile.userLinkUrlTextView

class PersonProfileFragment : Fragment() {

    private lateinit var hostingActivity: PeopleActivity
    private val answersAdapter = AnswersAdapter()

    companion object {

        fun newInstance(personProfileViewState: PersonProfileViewState): PersonProfileFragment {
            val personProfileFragment = PersonProfileFragment()
            val bundle = Bundle()
            bundle.putParcelable(PERSON_PROFILE_VIEW_STATE_KEY, personProfileViewState)
            personProfileFragment.arguments = bundle
            return personProfileFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_person_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hostingActivity = activity as PeopleActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        answersRecyclerView.layoutManager = LinearLayoutManager(context)
        answersRecyclerView.adapter = answersAdapter
        ViewCompat.setNestedScrollingEnabled(answersRecyclerView, false)

        dismissProfileButton.setOnClickListener { emitTagForDismiss(tag) }
        acceptProfileButton.setOnClickListener { emitTagForAccept(tag) }

        // this is save thanks to newInstance method
        render(arguments!!.getParcelable(PERSON_PROFILE_VIEW_STATE_KEY))
    }

    private fun emitTagForDismiss(tag: String?) {
        tag?.let { hostingActivity.negativeMeetSubject.onNext(it) }
    }

    private fun emitTagForAccept(tag: String?) {
        tag?.let { hostingActivity.positiveMeetSubject.onNext(it) }
    }

    private fun render(personProfileViewState: PersonProfileViewState) {
        with(personProfileViewState) {
            eventUserNameTextView.text = eventUserName
            setUserLinkUrl(userLinkUrl)
            if (profilePictureUrl.isNotBlank()) {
                loadProfilePicture(profilePictureUrl)
            }
            answersAdapter.setAnswerList(answerViewStateList)
        }
    }

    private fun loadProfilePicture(profilePictureUrl: String) {
        loadingPhotoTextView.visibility = View.VISIBLE
        GlideApp.with(this)
                .load(profilePictureUrl)
                .placeholder(R.drawable.profile_picture_shape)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable,
                                                 model: Any?, target:
                                                 Target<Drawable>,
                                                 dataSource: DataSource,
                                                 isFirstResource: Boolean): Boolean {
                        profilePictureImageView?.adjustViewBounds = true
                        loadingPhotoTextView?.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?,
                                              model: Any,
                                              target: Target<Drawable>,
                                              isFirstResource: Boolean): Boolean {
                        profilePictureImageView?.adjustViewBounds = false
                        profilePictureImageView?.setImageResource(R.drawable.profile_picture_shape)
                        loadingPhotoTextView?.visibility = View.GONE
                        return false
                    }
                })
                .into(profilePictureImageView)
    }

    private fun setUserLinkUrl(userLinkUrl: String) {
        if (userLinkUrl.isNotBlank()) {
            userLinkUrlTextView.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
            userLinkUrlTextView.text = userLinkUrl
        }
    }
}