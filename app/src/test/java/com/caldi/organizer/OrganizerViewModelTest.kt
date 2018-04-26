package com.caldi.organizer

import com.caldi.organizer.models.EventInfo
import com.caldi.organizer.models.Message
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class OrganizerViewModelTest {

    private val organizerInteractor: OrganizerInteractor = mock()
    private val organizerViewModel: OrganizerViewModel = OrganizerViewModel(organizerInteractor)

    private val newMessagesList = listOf(Message("30-03-2018", "test message 1", "1"))
    private val updatedMessagesList = listOf(Message("30-03-2018", "test message 1", "1"),
            Message("30-03-2018", "test message 2", "2"))

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        whenever(organizerInteractor.fetchEventInfo(any())).thenReturn(
                Observable.just(PartialOrganizerViewState.EventInfoFetched(EventInfo("testEventName",
                        "url/to/pic", "url/to/event")))
        )
        whenever(organizerInteractor.fetchMessagesBatch(any(), any())).thenReturn(
                Observable.just(PartialOrganizerViewState.MessagesListChanged(updatedMessagesList))
        )
        whenever(organizerInteractor.listenForNewMessages(any())).thenReturn(
                Observable.just(PartialOrganizerViewState.MessagesListChanged(newMessagesList))
        )
        whenever(organizerInteractor.stopListeningForNewMessages(any())).thenReturn(
                Observable.just(PartialOrganizerViewState.NewMessagesListenerRemoved())
        )
    }

    @Test
    fun testEventInfoFetching() {
        val organizerViewRobot = OrganizerViewRobot(organizerViewModel)

        organizerViewRobot.startView("testEventId")

        organizerViewRobot.triggerEventInfoFetching()

        organizerViewRobot.stopView()

        organizerViewRobot.startView("testEventId")

        organizerViewRobot.assertViewStates(
                OrganizerViewState(
                        progress = true),
                OrganizerViewState(
                        progress = true,
                        eventName = "testEventName",
                        eventImageUrl = "url/to/pic",
                        eventUrl = "url/to/event"),
                OrganizerViewState(
                        progress = true,
                        eventName = "testEventName",
                        eventImageUrl = "url/to/pic",
                        eventUrl = "url/to/event"))
    }

    @Test
    fun testMessagesBatchFetching() {
        val organizerViewRobot = OrganizerViewRobot(organizerViewModel)

        organizerViewRobot.startView("testEventId")

        organizerViewRobot.triggerBatchFetching("30-03-2018")

        organizerViewRobot.stopView()

        organizerViewRobot.startView("testEventId")

        organizerViewRobot.assertViewStates(
                OrganizerViewState(progress = true),
                OrganizerViewState(messagesList = updatedMessagesList),
                OrganizerViewState(messagesList = updatedMessagesList)
        )
    }

    @Test
    fun testNewMessagesListenerSet() {
        val organizerViewRobot = OrganizerViewRobot(organizerViewModel)

        organizerViewRobot.startView("testEventId")

        organizerViewRobot.triggerNewMessagesListeningToggle(true)

        organizerViewRobot.stopView()

        organizerViewRobot.startView("testEventId")

        organizerViewRobot.assertViewStates(
                OrganizerViewState(progress = true),
                OrganizerViewState(messagesList = newMessagesList),
                OrganizerViewState(messagesList = newMessagesList)
        )
    }

    @Test
    fun testNewMessagesListenerRemoval() {
        val organizerViewRobot = OrganizerViewRobot(organizerViewModel)

        organizerViewRobot.startView("testEventId")

        organizerViewRobot.triggerNewMessagesListeningToggle(false)

        organizerViewRobot.stopView()

        organizerViewRobot.startView("testEventId")

        organizerViewRobot.assertViewStates(
                OrganizerViewState(progress = true),
                OrganizerViewState(progress = true),
                OrganizerViewState(progress = true)
        )
    }

    @Test
    fun testBatchAndEventInfoFetchingCombinedWithSettingListener() {
        val organizerViewRobot = OrganizerViewRobot(organizerViewModel)

        organizerViewRobot.startView("testEventId")

        organizerViewRobot.triggerNewMessagesListeningToggle(true)

        organizerViewRobot.triggerBatchFetching("30-03-2018")

        organizerViewRobot.triggerEventInfoFetching()

        organizerViewRobot.stopView()

        organizerViewRobot.startView("testEventId")

        organizerViewRobot.assertViewStates(
                OrganizerViewState(
                        progress = true),
                OrganizerViewState(
                        progress = false,
                        messagesList = newMessagesList),
                OrganizerViewState(
                        progress = false,
                        messagesList = updatedMessagesList),
                OrganizerViewState(
                        progress = false,
                        eventName = "testEventName",
                        eventImageUrl = "url/to/pic",
                        eventUrl = "url/to/event",
                        messagesList = updatedMessagesList),
                OrganizerViewState(
                        progress = false,
                        eventName = "testEventName",
                        eventImageUrl = "url/to/pic",
                        eventUrl = "url/to/event",
                        messagesList = updatedMessagesList)
        )
    }
}