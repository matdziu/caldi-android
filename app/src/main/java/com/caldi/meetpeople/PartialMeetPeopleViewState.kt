package com.caldi.meetpeople

import com.caldi.meetpeople.models.AttendeeProfile

sealed class PartialMeetPeopleViewState {

    class SuccessfulProfileFetchState(val attendeesProfilesList: List<AttendeeProfile> = listOf()) : PartialMeetPeopleViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialMeetPeopleViewState()

    class ProgressState : PartialMeetPeopleViewState()
}