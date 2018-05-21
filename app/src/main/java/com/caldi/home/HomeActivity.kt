package com.caldi.home

import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.caldi.R
import com.caldi.addevent.AddEventActivity
import com.caldi.base.BaseOverflowActivity
import com.caldi.constants.ADD_EVENT_REQUEST_CODE
import com.caldi.constants.NOTIFICATION_TOKEN_ACTION
import com.caldi.constants.NOTIFICATION_TOKEN_KEY
import com.caldi.factories.HomeViewModelFactory
import com.caldi.home.list.EventsAdapter
import com.caldi.home.models.Event
import com.caldi.onboarding.OnboardingInfo
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.iid.FirebaseInstanceId
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_home.addEventButton
import kotlinx.android.synthetic.main.activity_home.eventsRecyclerView
import kotlinx.android.synthetic.main.activity_home.noEventsTextView
import kotlinx.android.synthetic.main.activity_home.progressBar
import kotlinx.android.synthetic.main.toolbar.logo
import javax.inject.Inject

class HomeActivity : BaseOverflowActivity(), HomeView {

    private val eventsAdapter: EventsAdapter = EventsAdapter()

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var eventsFetchTriggerSubject: Subject<Boolean>

    private lateinit var notificationTokenSubject: Subject<String>

    @Inject
    lateinit var homeViewModelFactory: HomeViewModelFactory

    private var init: Boolean = true

    private val notificationTokenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                notificationTokenSubject.onNext(intent.getStringExtra(NOTIFICATION_TOKEN_KEY))
                enableAllNotifications()
            } catch (e: UninitializedPropertyAccessException) {
                Log.e("notifications", e.message)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_home)
        super.onCreate(savedInstanceState)
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)

        homeViewModel = ViewModelProviders.of(this, homeViewModelFactory)[HomeViewModel::class.java]

        eventsRecyclerView.adapter = eventsAdapter
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)

        addEventButton.setOnClickListener {
            startActivityForResult(Intent(this, AddEventActivity::class.java), ADD_EVENT_REQUEST_CODE)
        }

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(notificationTokenReceiver, IntentFilter(NOTIFICATION_TOKEN_ACTION))

        showOnboarding(OnboardingInfo(logo, getString(R.string.onboarding_home_screen), "homeScreenOnboarding"))
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        homeViewModel.bind(this)

        if (init) {
            eventsFetchTriggerSubject.onNext(true)
            FirebaseInstanceId.getInstance().token?.let {
                notificationTokenSubject.onNext(it)
                enableAllNotifications()
            }
        }
    }

    private fun initEmitters() {
        eventsFetchTriggerSubject = PublishSubject.create()
        notificationTokenSubject = PublishSubject.create()
    }

    override fun onStop() {
        init = false
        homeViewModel.unbind()
        super.onStop()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationTokenReceiver)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_EVENT_REQUEST_CODE && resultCode == RESULT_OK) {
            eventsFetchTriggerSubject.onNext(true)
        }
    }

    override fun emitEventsFetchTrigger(): Observable<Boolean> = eventsFetchTriggerSubject

    override fun emitNotificationToken(): Observable<String> = notificationTokenSubject

    override fun render(homeViewState: HomeViewState) {
        with(homeViewState) {
            showProgressBar(inProgress)
            showError(error, dismissToast)
            setEventList(inProgress, error, eventList)
        }
    }

    private fun setEventList(progress: Boolean, error: Boolean, eventList: List<Event>) {
        noEventsTextView.visibility = View.GONE
        if (!error && !progress && eventList.isNotEmpty()) {
            eventsAdapter.setEventList(eventList)
        } else if (!error && !progress && eventList.isEmpty()) {
            noEventsTextView.visibility = View.VISIBLE
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
            eventsRecyclerView.visibility = View.GONE
            addEventButton.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            eventsRecyclerView.visibility = View.VISIBLE
            addEventButton.visibility = View.VISIBLE
        }
    }

    private fun showError(error: Boolean, dismissToast: Boolean) {
        if (error && !dismissToast) {
            Toast.makeText(this, getString(R.string.event_list_fetching_error), Toast.LENGTH_SHORT).show()
        }
    }
}