package com.caldi.eventprofile

import com.caldi.eventprofile.models.EventProfileData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import junit.framework.Assert
import java.io.File

class EventProfileViewRobot(eventProfileViewModel: EventProfileViewModel) {

    private val renderedStates = arrayListOf<EventProfileViewState>()

    private val profileFetchingTriggerObservable = PublishSubject.create<String>()

    private val profilePictureFileObservable = PublishSubject.create<File>()

    private val inputDataObservable = PublishSubject.create<EventProfileData>()

    private val eventProfileView = object : EventProfileView {

        override fun emitProfilePictureFile(): Observable<File> = profilePictureFileObservable

        override fun emitEventProfileFetchingTrigger(): Observable<String> = profileFetchingTriggerObservable

        override fun emitInputData(): Observable<EventProfileData> = inputDataObservable

        override fun render(eventProfileViewState: EventProfileViewState) {
            renderedStates.add(eventProfileViewState)
        }
    }

    init {
        eventProfileViewModel.bind(eventProfileView)
    }

    fun sendProfilePictureFile(profilePicture: File) {
        profilePictureFileObservable.onNext(profilePicture)
    }

    fun fetchEventProfile(eventId: String) {
        profileFetchingTriggerObservable.onNext(eventId)
    }

    fun emitInputData(eventProfileData: EventProfileData) {
        inputDataObservable.onNext(eventProfileData)
    }

    fun assertViewStates(vararg expectedStates: EventProfileViewState) {
        Assert.assertEquals(expectedStates.toCollection(arrayListOf()), renderedStates)
    }
}