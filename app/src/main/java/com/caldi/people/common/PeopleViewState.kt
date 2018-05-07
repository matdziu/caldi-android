package com.caldi.people.common

import com.caldi.common.states.PersonProfileViewState

data class PeopleViewState(val progress: Boolean = false,
                           val error: Boolean = false,
                           val dismissToast: Boolean = false,
                           val eventProfileBlank: Boolean = false,
                           val eventQuestions: Map<String, String> = mapOf(),
                           val personProfileViewStateList: List<PersonProfileViewState> = listOf())