package com.caldi.home

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.caldi.R
import com.caldi.addevent.AddEventActivity
import com.caldi.base.BaseOverflowActivity
import com.caldi.constants.ADD_EVENT_REQUEST_CODE
import com.caldi.factories.HomeViewModelFactory
import com.caldi.home.list.EventsAdapter
import com.caldi.home.models.Event
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_home.addEventButton
import kotlinx.android.synthetic.main.activity_home.eventsRecyclerView
import kotlinx.android.synthetic.main.activity_home.noEventsTextView
import kotlinx.android.synthetic.main.activity_home.progressBar
import javax.inject.Inject

class HomeActivity : BaseOverflowActivity(), HomeView {

    private val eventsAdapter: EventsAdapter = EventsAdapter()

    private lateinit var homeViewModel: HomeViewModel

    private val eventsFetchTriggerObservable: Subject<Boolean> = PublishSubject.create()

    @Inject
    lateinit var homeViewModelFactory: HomeViewModelFactory

    private var forceEventsFetching: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_home)
        super.onCreate(savedInstanceState)

        homeViewModel = ViewModelProviders.of(this, homeViewModelFactory)[HomeViewModel::class.java]

        eventsRecyclerView.adapter = eventsAdapter
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)

        addEventButton.setOnClickListener {
            startActivityForResult(Intent(this, AddEventActivity::class.java), ADD_EVENT_REQUEST_CODE)
        }
    }

    override fun onStart() {
        super.onStart()
        homeViewModel.bind(this)
        eventsFetchTriggerObservable.onNext(forceEventsFetching)
    }

    override fun onStop() {
        forceEventsFetching = false
        homeViewModel.unbind()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_EVENT_REQUEST_CODE && resultCode == RESULT_OK) {
            eventsFetchTriggerObservable.onNext(true)
        }
    }

    override fun emitEventsFetchTrigger(): Observable<Boolean> = eventsFetchTriggerObservable


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