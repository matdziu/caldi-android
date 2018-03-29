package com.caldi.organizer

import com.caldi.organizer.models.EventInfo
import com.caldi.organizer.models.Message

sealed class PartialOrganizerViewState {

    class EventInfoFetched(val eventInfo: EventInfo) : PartialOrganizerViewState()

    class MessagesListChanged(val updatedMessagesList: List<Message>) : PartialOrganizerViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialOrganizerViewState()

    class NewMessagesListenerRemoved : PartialOrganizerViewState()
}