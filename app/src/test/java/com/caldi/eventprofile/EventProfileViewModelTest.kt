package com.caldi.eventprofile

import com.caldi.eventprofile.list.QuestionViewState
import com.caldi.eventprofile.models.Answer
import com.caldi.eventprofile.models.EventProfileData
import com.caldi.eventprofile.models.Question
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.concurrent.TimeUnit

class EventProfileViewModelTest {

    private val eventProfileInteractor: EventProfileInteractor = mock()
    private val eventProfileViewModel: EventProfileViewModel = EventProfileViewModel(eventProfileInteractor)

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testEventProfileDataFetching() {
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
                        questionViewStateList = listOf(QuestionViewState("What are you looking for here?", "Looking for party!", "1"))),
                EventProfileViewState(eventUserName = "Matt the Android Dev", renderEventName = false,
                        questionViewStateList = listOf(QuestionViewState("What are you looking for here?", "Looking for party!", "1")))
        )
    }
}