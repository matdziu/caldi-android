package com.caldi.organizer

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.factories.OrganizerViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class OrganizerActivity : BaseDrawerActivity(), OrganizerView {

    private val batchFetchTriggerSubject = PublishSubject.create<String>()

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
        organizerViewModel.bind(this)
    }

    override fun onStop() {
        organizerViewModel.unbind()
        super.onStop()
    }

    override fun emitBatchFetchTrigger(): Observable<String> = batchFetchTriggerSubject

    override fun render(organizerViewState: OrganizerViewState) {

    }
}