package com.caldi.meetpeople.personprofile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caldi.R
import com.caldi.base.BasePeopleActivity
import com.caldi.common.states.PersonProfileViewState
import com.caldi.constants.PERSON_PROFILE_VIEW_STATE_KEY
import com.caldi.meetpeople.list.AnswersAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_person_profile.acceptProfileButton
import kotlinx.android.synthetic.main.fragment_person_profile.answersRecyclerView
import kotlinx.android.synthetic.main.fragment_person_profile.dismissProfileButton
import kotlinx.android.synthetic.main.fragment_person_profile.divider
import kotlinx.android.synthetic.main.fragment_person_profile.eventUserNameTextView
import kotlinx.android.synthetic.main.fragment_person_profile.loadingPhotoTextView
import kotlinx.android.synthetic.main.fragment_person_profile.profilePictureImageView
import kotlinx.android.synthetic.main.fragment_person_profile.userLinkUrlTextView
import java.lang.Exception

class PersonProfileFragment : Fragment() {

    private lateinit var hostingActivity: BasePeopleActivity
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
        hostingActivity = activity as BasePeopleActivity
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
                loadingPhotoTextView.visibility = View.VISIBLE
                Picasso.get().load(profilePictureUrl).placeholder(R.drawable.profile_picture_shape)
                        .into(profilePictureImageView, object : Callback {
                            override fun onSuccess() {
                                profilePictureImageView?.adjustViewBounds = true
                                loadingPhotoTextView?.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                profilePictureImageView?.adjustViewBounds = false
                                profilePictureImageView?.setImageResource(R.drawable.profile_picture_shape)
                                loadingPhotoTextView?.visibility = View.GONE
                            }
                        })
            }
            answersAdapter.setAnswerList(answerViewStateList)
        }
    }

    private fun setUserLinkUrl(userLinkUrl: String) {
        if (userLinkUrl.isNotBlank()) {
            userLinkUrlTextView.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
            userLinkUrlTextView.text = userLinkUrl
        }
    }
}