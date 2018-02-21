package com.caldi.home

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.caldi.R
import com.caldi.addevent.AddEventActivity
import com.caldi.base.BaseDrawerActivity
import com.caldi.factories.HomeViewModelFactory
import com.caldi.home.list.EventsAdapter
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_home.addEventButton
import kotlinx.android.synthetic.main.activity_home.eventsRecyclerView
import kotlinx.android.synthetic.main.activity_home.progressBar
import javax.inject.Inject

class HomeActivity : BaseDrawerActivity(), HomeView {

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
        setNavigationSelection(R.id.events_item)

        homeViewModel = ViewModelProviders.of(this, homeViewModelFactory)[HomeViewModel::class.java]

        eventsRecyclerView.adapter = eventsAdapter
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)

        addEventButton.setOnClickListener {
            startActivity(Intent(this, AddEventActivity::class.java))
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

    override fun emitEventsFetchTrigger(): Observable<Boolean> = eventsFetchTriggerObservable

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

    override fun render(homeViewState: HomeViewState) {
        showProgressBar(homeViewState.inProgress)
        if (homeViewState.error && !homeViewState.dismissToast) {
            Toast.makeText(this, getString(R.string.event_list_fetching_error), Toast.LENGTH_SHORT).show()
        }
        if (!homeViewState.error && !homeViewState.inProgress) {
            eventsAdapter.setEventList(homeViewState.eventList)
        }
    }
}