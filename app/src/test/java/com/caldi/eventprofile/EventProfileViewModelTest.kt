package com.caldi.eventprofile

import com.caldi.eventprofile.list.QuestionViewState
import com.caldi.base.models.Answer
import com.caldi.eventprofile.models.EventProfileData
import com.caldi.base.models.Question
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

class EventProfileViewModelTest {

    private val eventProfileInteractor: EventProfileInteractor = mock()
    private val eventProfileViewModel: EventProfileViewModel = EventProfileViewModel(eventProfileInteractor)

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testEventProfileDataFetchingSuccess() {
        val eventProfileData = EventProfileData("Matt the Android Dev",
                listOf(Answer("1", "Looking for party!", true)),
                listOf(Question("1", "What are you looking for here?")))
        whenever(eventProfileInteractor.fetchEventProfile(any())).thenReturn(
                Observable.timer(100, TimeUnit.MILLISECONDS)
                        .map { PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, false) }
                        .startWith(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, true))
                        as Observable<PartialEventProfileViewState>
        )

        val eventProfileViewRobot = EventProfileViewRobot(eventProfileViewModel)

        eventProfileViewRobot.fetchEventProfile("geecon")
        eventProfileViewRobot.assertViewStates(
                EventProfileViewState(),
                EventProfileViewState(progress = true),
                EventProfileViewState(eventUserName = "Matt the Android Dev", renderEventName = true,
                        questionViewStateList = listOf(QuestionViewState("What are you looking for here?",
                                "Looking for party!", "1"))),
                EventProfileViewState(eventUserName = "Matt the Android Dev", renderEventName = false,
                        questionViewStateList = listOf(QuestionViewState("What are you looking for here?",
                                "Looking for party!", "1")))
        )
    }

    @Test
    fun testEventProfileUpdateInvalid() {
        val eventProfileData = EventProfileData("Matt the Android Dev",
                listOf(Answer("1", "Looking for party!", true)),
                listOf(Question("1", "What are you looking for here?")))
        whenever(eventProfileInteractor.fetchEventProfile(any())).thenReturn(
                Observable.timer(100, TimeUnit.MILLISECONDS)
                        .map { PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, false) }
                        .startWith(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, true))
                        as Observable<PartialEventProfileViewState>
        )
        val eventProfileDataEmptyAnswer = EventProfileData("Matt the Android Dev",
                listOf(Answer("1", " ", true)),
                listOf(Question("1", "What are you looking for here?")))

        val eventProfileDataEmptyName = EventProfileData(" ",
                listOf(Answer("1", "Looking for a party!", true)),
                listOf(Question("1", "What are you looking for here?")))

        val eventProfileViewRobot = EventProfileViewRobot(eventProfileViewModel)

        eventProfileViewRobot.fetchEventProfile("droidcon")
        eventProfileViewRobot.emitInputData(eventProfileDataEmptyAnswer)
        eventProfileViewRobot.emitInputData(eventProfileDataEmptyName)
        eventProfileViewRobot.assertViewStates(
                EventProfileViewState(),
                EventProfileViewState(progress = true),
                EventProfileViewState(eventUserName = "Matt the Android Dev", renderEventName = true,
                        questionViewStateList = listOf(QuestionViewState("What are you looking for here?",
                                "Looking for party!", "1"))),
                EventProfileViewState(eventUserName = "Matt the Android Dev", renderEventName = false,
                        questionViewStateList = listOf(QuestionViewState("What are you looking for here?",
                                "Looking for party!", "1"))),
                EventProfileViewState(eventUserName = "Matt the Android Dev", renderEventName = false,
                        questionViewStateList = listOf(QuestionViewState("What are you looking for here?",
                                " ", "1", false))),
                EventProfileViewState(eventUserName = " ", renderEventName = false, eventUserNameValid = false,
                        questionViewStateList = listOf(QuestionViewState("What are you looking for here?",
                                "Looking for a party!", "1", true)))
        )
    }

    @Test
    fun testEventProfileUpdateValid() {
        val eventProfileData = EventProfileData("Matt the Android Dev",
                listOf(Answer("1", "Looking for party!", true)),
                listOf(Question("1", "What are you looking for here?")))
        whenever(eventProfileInteractor.updateEventProfile(any(), any())).thenReturn(
                Observable.timer(100, TimeUnit.MILLISECONDS)
                        .map { PartialEventProfileViewState.SuccessfulUpdateState(true) }
                        .startWith(PartialEventProfileViewState.SuccessfulUpdateState()) as Observable<PartialEventProfileViewState>
        )

        val eventProfileViewRobot = EventProfileViewRobot(eventProfileViewModel)

        eventProfileViewRobot.emitInputData(eventProfileData)
        eventProfileViewRobot.assertViewStates(
                EventProfileViewState(),
                EventProfileViewState(progress = true),
                EventProfileViewState(successUpload = true, dismissToast = false),
                EventProfileViewState(successUpload = true, dismissToast = true)
        )
    }

    @Test
    fun testProfilePictureUploadSuccess() {
        whenever(eventProfileInteractor.uploadProfilePicture(any(), any())).thenReturn(Observable.just(
                PartialEventProfileViewState.SuccessfulPictureUploadState("this/is/pic/url")
        ))

        val eventProfileViewRobot = EventProfileViewRobot(eventProfileViewModel)

        eventProfileViewRobot.sendProfilePictureFile(File("this/is/pic/filepath"))
        eventProfileViewRobot.assertViewStates(
                EventProfileViewState(),
                EventProfileViewState(progress = true),
                EventProfileViewState(profilePictureUrl = "this/is/pic/url")
        )
    }

    @Test
    fun testProfilePictureUploadError() {
        whenever(eventProfileInteractor.uploadProfilePicture(any(), any())).thenReturn(
                Observable.timer(100, TimeUnit.MILLISECONDS)
                        .map { PartialEventProfileViewState.ErrorState(true) }
                        .startWith(PartialEventProfileViewState.ErrorState())
                        as Observable<PartialEventProfileViewState>
        )

        val eventProfileViewRobot = EventProfileViewRobot(eventProfileViewModel)

        eventProfileViewRobot.sendProfilePictureFile(File("this/is/pic/filepath"))
        eventProfileViewRobot.assertViewStates(
                EventProfileViewState(),
                EventProfileViewState(progress = true),
                EventProfileViewState(error = true),
                EventProfileViewState(error = true, dismissToast = true)
        )
    }
}