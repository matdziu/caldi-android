package com.caldi.organizer

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class OrganizerViewModel(private val organizerInteractor: OrganizerInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(OrganizerViewState(progress = true))

    fun bind(organizerView: OrganizerView, eventId: String) {
        val newMessagesListeningToggleObservable = organizerView.emitNewMessagesListeningToggle()
                .flatMap {
                    if (it) organizerInteractor.listenForNewMessages(eventId)
                    else organizerInteractor.stopListeningForNewMessages(eventId)
                }

        val eventInfoFetchTriggerObservable = organizerView.emitEventInfoFetchTrigger()
                .flatMap { organizerInteractor.fetchEventInfo(eventId) }

        val batchFetchTriggerObservable = organizerView.emitBatchFetchTrigger()
                .flatMap { organizerInteractor.fetchMessagesBatch(eventId, it) }

        val mergedObservable = Observable.merge(arrayListOf(
                newMessagesListeningToggleObservable,
                eventInfoFetchTriggerObservable,
                batchFetchTriggerObservable))
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { organizerView.render(it) })
    }

    private fun reduce(previousState: OrganizerViewState, partialState: PartialOrganizerViewState)
            : OrganizerViewState {
        return when (partialState) {
            is PartialOrganizerViewState.EventInfoFetched -> previousState.copy(
                    eventName = partialState.eventInfo.name,
                    eventImageUrl = partialState.eventInfo.imageUrl,
                    eventUrl = partialState.eventInfo.eventUrl)
            is PartialOrganizerViewState.NewMessagesListenerRemoved -> previousState
            is PartialOrganizerViewState.ErrorState -> previousState.copy(
                    error = true,
                    dismissToast = partialState.dismissToast)
            is PartialOrganizerViewState.MessagesListChanged -> previousState.copy(
                    progress = false,
                    error = false,
                    messagesList = partialState.updatedMessagesList
            )
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}