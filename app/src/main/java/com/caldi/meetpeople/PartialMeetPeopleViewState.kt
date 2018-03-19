package com.caldi.meetpeople

import com.caldi.meetpeople.models.AttendeeProfile

sealed class PartialMeetPeopleViewState {

    class SuccessfulAttendeesFetchState(val attendeesProfilesList: List<AttendeeProfile> = listOf()) : PartialMeetPeopleViewState()

    class SuccessfulMetAttendeeSave : PartialMeetPeopleViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialMeetPeopleViewState()

    class ProgressState : PartialMeetPeopleViewState()
}