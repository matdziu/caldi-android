package com.caldi.home

import com.caldi.home.models.Event

data class HomeViewState(val inProgress: Boolean = false,
                         val error: Boolean = false,
                         val dismissToast: Boolean = false,
                         val eventList: List<Event> = listOf())