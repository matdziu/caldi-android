package com.caldi.meetpeople

import com.caldi.meetpeople.personprofile.PersonProfileViewState

data class MeetPeopleViewState(val progress: Boolean = false,
                               val error: Boolean = false,
                               val dismissToast: Boolean = false,
                               val eventProfileBlank: Boolean = false,
                               val personProfileViewStateList: List<PersonProfileViewState> = listOf())