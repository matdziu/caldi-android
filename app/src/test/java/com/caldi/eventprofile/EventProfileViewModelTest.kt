package com.caldi.eventprofile

import com.caldi.common.models.EventProfileData
import com.caldi.eventprofile.list.QuestionViewState
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.io.File

class EventProfileViewModelTest {

    private val eventProfileInteractor: EventProfileInteractor = mock()
    private val eventProfileViewModel: EventProfileViewModel = EventProfileViewModel(eventProfileInteractor)

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testEventProfileDataFetchingSuccess() {
        val questions = mapOf("1" to "What are you looking for?")
        val eventProfileData = EventProfileData(
                "123",
                "Matt the Android Dev",
                mapOf("1" to "Looking for party!"),
                "url/to/pic",
                "user/link")
        whenever(eventProfileInteractor.fetchEventProfile(any())).thenReturn(
                Observable.just(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, questions, false))
                        .startWith(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, questions))
                        as Observable<PartialEventProfileViewState>
        )

        val eventProfileViewRobot = EventProfileViewRobot(eventProfileViewModel)

        eventProfileViewRobot.fetchEventProfile("geecon")
        eventProfileViewRobot.assertViewStates(
                EventProfileViewState(),
                EventProfileViewState(
                        progress = true),
                EventProfileViewState(
                        eventUserName = "Matt the Android Dev",
                        profilePictureUrl = "url/to/pic",
                        userLinkUrl = "user/link",
                        renderInputs = true,
                        questionViewStates = listOf(QuestionViewState("What are you looking for?",
                                "Looking for party!", "1"))),
                EventProfileViewState(
                        eventUserName = "Matt the Android Dev",
                        profilePictureUrl = "url/to/pic",
                        userLinkUrl = "user/link",
                        renderInputs = false,
                        questionViewStates = listOf(QuestionViewState("What are you looking for?",
                                "Looking for party!", "1")))
        )
    }

    @Test
    fun testEventProfileUpdateInvalid() {
        val questions = mapOf("1" to "What are you looking for?")
        val eventProfileData = EventProfileData(
                "123",
                "Matt the Android Dev",
                mapOf("1" to "Looking for a party!"),
                "url/to/pic",
                "user/link")
        whenever(eventProfileInteractor.fetchEventProfile(any())).thenReturn(
                Observable.just(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, questions, false))
                        .startWith(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, questions))
                        as Observable<PartialEventProfileViewState>
        )
        val eventProfileDataEmptyAnswer = EventProfileData(
                "123",
                "Matt the Android Dev",
                mapOf("1" to " "),
                "url/to/pic",
                "user/link")

        val eventProfileDataEmptyName = EventProfileData(
                "123",
                " ",
                mapOf("1" to "Looking for a party!"),
                "url/to/pic",
                "user/link")

        val eventProfileViewRobot = EventProfileViewRobot(eventProfileViewModel)

        eventProfileViewRobot.fetchEventProfile("droidcon")

        eventProfileViewRobot.emitInputData(eventProfileDataEmptyAnswer)

        eventProfileViewRobot.emitInputData(eventProfileDataEmptyName)

        val fetchedQuestionViewStateList = listOf(QuestionViewState("What are you looking for?",
                "Looking for a party!", "1"))
        val errorQuestionViewState = listOf(QuestionViewState("What are you looking for?",
                " ", "1", answerValid = false))
        eventProfileViewRobot.assertViewStates(
                EventProfileViewState(),
                EventProfileViewState(progress = true),
                EventProfileViewState(
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        renderInputs = true,
                        profilePictureUrl = "url/to/pic",
                        userLinkUrl = "user/link",
                        questionViewStates = fetchedQuestionViewStateList),
                EventProfileViewState(
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        renderInputs = false,
                        profilePictureUrl = "url/to/pic",
                        userLinkUrl = "user/link",
                        questionViewStates = fetchedQuestionViewStateList),
                EventProfileViewState(
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        renderInputs = true,
                        profilePictureUrl = "url/to/pic",
                        userLinkUrl = "user/link",
                        questionViewStates = errorQuestionViewState),
                EventProfileViewState(
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        renderInputs = false,
                        profilePictureUrl = "url/to/pic",
                        userLinkUrl = "user/link",
                        questionViewStates = errorQuestionViewState),
                EventProfileViewState(
                        eventUserName = " ",
                        eventUserNameValid = false,
                        renderInputs = true,
                        profilePictureUrl = "url/to/pic",
                        userLinkUrl = "user/link",
                        questionViewStates = fetchedQuestionViewStateList),
                EventProfileViewState(
                        eventUserName = " ",
                        eventUserNameValid = false,
                        renderInputs = false,
                        profilePictureUrl = "url/to/pic",
                        userLinkUrl = "user/link",
                        questionViewStates = fetchedQuestionViewStateList)
        )
    }

    @Test
    fun testEventProfileUpdateValid() {
        val questions = mapOf("1" to "What are you looking for?")
        val eventProfileData = EventProfileData(
                "123",
                "Matt the Android Dev",
                mapOf("1" to "Looking for party!"))
        whenever(eventProfileInteractor.updateEventProfile(any(), any())).thenReturn(
                Observable.just(PartialEventProfileViewState.SuccessfulUpdateState(true))
                        .startWith(PartialEventProfileViewState.SuccessfulUpdateState())
                        as Observable<PartialEventProfileViewState>
        )
        whenever(eventProfileInteractor.fetchEventProfile(any())).thenReturn(
                Observable.just(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, questions, false))
                        .startWith(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, questions))
                        as Observable<PartialEventProfileViewState>
        )

        val eventProfileViewRobot = EventProfileViewRobot(eventProfileViewModel)

        eventProfileViewRobot.fetchEventProfile("droidcon")

        eventProfileViewRobot.emitInputData(eventProfileData)

        val questionViewStateList = listOf(QuestionViewState("What are you looking for?",
                "Looking for party!", "1", true))
        eventProfileViewRobot.assertViewStates(
                EventProfileViewState(),
                EventProfileViewState(progress = true),
                EventProfileViewState(
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        questionViewStates = questionViewStateList,
                        renderInputs = true
                ),
                EventProfileViewState(
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        questionViewStates = questionViewStateList,
                        renderInputs = false
                ),
                EventProfileViewState(
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        questionViewStates = questionViewStateList,
                        renderInputs = true
                ),
                EventProfileViewState(
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        questionViewStates = questionViewStateList,
                        renderInputs = false
                ),
                EventProfileViewState(
                        progress = true,
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        questionViewStates = questionViewStateList,
                        renderInputs = false
                ),
                EventProfileViewState(
                        updateSuccess = true,
                        dismissToast = false,
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        questionViewStates = questionViewStateList,
                        renderInputs = false
                ),
                EventProfileViewState(
                        updateSuccess = true,
                        dismissToast = true,
                        eventUserName = "Matt the Android Dev",
                        eventUserNameValid = true,
                        questionViewStates = questionViewStateList,
                        renderInputs = false
                )
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
                Observable.just(PartialEventProfileViewState.ErrorState(true))
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