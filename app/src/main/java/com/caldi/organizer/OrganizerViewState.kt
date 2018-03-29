package com.caldi.organizer

import com.caldi.organizer.models.Message

data class OrganizerViewState(val messagesList: List<Message> = listOf(),
                              val error: Boolean = false,
                              val dismissToast: Boolean = false)