package com.caldi.people.common

import com.caldi.common.models.EventProfileData

sealed class PartialPeopleViewState {

    class SuccessfulAttendeesFetchState(val attendeesProfilesList: List<EventProfileData> = listOf()) : PartialPeopleViewState()

    class SuccessfulMetAttendeeSave : PartialPeopleViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialPeopleViewState()

    class ProgressState : PartialPeopleViewState()

    class BlankEventProfileState(val dismissToast: Boolean = false) : PartialPeopleViewState()

    class SuccessfulQuestionsFetchState(val questions: Map<String, String>) : PartialPeopleViewState()
}