package com.caldi.people.common

import com.caldi.people.common.models.AttendeeProfile

sealed class PartialPeopleViewState {

    class SuccessfulAttendeesFetchState(val attendeesProfilesList: List<AttendeeProfile> = listOf()) : PartialPeopleViewState()

    class SuccessfulMetAttendeeSave : PartialPeopleViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialPeopleViewState()

    class ProgressState : PartialPeopleViewState()

    class BlankEventProfileState(val dismissToast: Boolean = false) : PartialPeopleViewState()
}