package com.caldi.organizer

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.extensions.getCurrentISODate
import com.caldi.factories.OrganizerViewModelFactory
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_organizer.messagesRecyclerView
import kotlinx.android.synthetic.main.activity_organizer.organizerInfoImageView
import kotlinx.android.synthetic.main.activity_organizer.organizerInfoTextView
import kotlinx.android.synthetic.main.activity_organizer.organizerInfoView
import kotlinx.android.synthetic.main.activity_organizer.progressBar
import javax.inject.Inject

class OrganizerActivity : BaseDrawerActivity(), OrganizerView {

    private val batchFetchTriggerSubject = PublishSubject.create<String>()

    private val eventInfoFetchTriggerSubject = PublishSubject.create<Boolean>()

    private val newMessagesListeningToggleSubject = PublishSubject.create<Boolean>()

    private var init = true

    private lateinit var organizerViewModel: OrganizerViewModel

    @Inject
    lateinit var organizerViewModelFactory: OrganizerViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_organizer)
        super.onCreate(savedInstanceState)

        organizerViewModel = ViewModelProviders.of(this, organizerViewModelFactory)[OrganizerViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        setNavigationSelection(R.id.organizer_item)
        organizerViewModel.bind(this, eventId)
        if (init) {
            batchFetchTriggerSubject.onNext(getCurrentISODate())
            eventInfoFetchTriggerSubject.onNext(true)
            newMessagesListeningToggleSubject.onNext(true)
            init = false
        }
    }

    override fun onStop() {
        organizerViewModel.unbind()
        super.onStop()
    }

    override fun onDestroy() {
        newMessagesListeningToggleSubject.onNext(false)
        super.onDestroy()
    }

    override fun emitBatchFetchTrigger(): Observable<String> = batchFetchTriggerSubject

    override fun emitEventInfoFetchTrigger(): Observable<Boolean> = eventInfoFetchTriggerSubject

    override fun emitNewMessagesListeningToggle(): Observable<Boolean> = newMessagesListeningToggleSubject

    override fun render(organizerViewState: OrganizerViewState) {
        with(organizerViewState) {
            showProgressBar(progress)
            organizerInfoTextView.text = eventName
            loadEventImage(eventImageUrl)
        }
    }

    private fun loadEventImage(eventImageUrl: String) {
        if (eventImageUrl.isNotEmpty()) {
            Picasso.get()
                    .load(eventImageUrl)
                    .placeholder(R.drawable.event_placeholder)
                    .into(organizerInfoImageView)
        }
    }

    private fun showProgressBar(show: Boolean) {
        progressBar.visibility = View.GONE
        if (show) {
            progressBar.visibility = View.VISIBLE
            messagesRecyclerView.visibility = View.GONE
            organizerInfoView.visibility = View.GONE
        } else {
            messagesRecyclerView.visibility = View.VISIBLE
            organizerInfoView.visibility = View.VISIBLE
        }
    }
}