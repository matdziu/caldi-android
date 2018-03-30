package com.caldi.organizer

import com.caldi.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class OrganizerViewRobot(private val organizerViewModel: OrganizerViewModel)
    : BaseViewRobot<OrganizerViewState>() {

    private val newMessagesListenerSubject = PublishSubject.create<Boolean>()

    private val eventInfoFetchSubject = PublishSubject.create<Boolean>()

    private val batchFetchTriggerSubject = PublishSubject.create<String>()

    private val organizerView = object : OrganizerView {

        override fun emitNewMessagesListeningToggle(): Observable<Boolean> = newMessagesListenerSubject

        override fun emitEventInfoFetchTrigger(): Observable<Boolean> = eventInfoFetchSubject

        override fun emitBatchFetchTrigger(): Observable<String> = batchFetchTriggerSubject

        override fun render(organizerViewState: OrganizerViewState) {
            renderedStates.add(organizerViewState)
        }
    }

    fun startView(eventId: String) {
        organizerViewModel.bind(organizerView, eventId)
    }

    fun stopView() {
        organizerViewModel.unbind()
    }

    fun triggerBatchFetching(fromTimestamp: String) {
        batchFetchTriggerSubject.onNext(fromTimestamp)
    }

    fun triggerEventInfoFetching() {
        eventInfoFetchSubject.onNext(true)
    }

    fun triggerNewMessagesListeningToggle(listen: Boolean) {
        newMessagesListenerSubject.onNext(listen)
    }
}