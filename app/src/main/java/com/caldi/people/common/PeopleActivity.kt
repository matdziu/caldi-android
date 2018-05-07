package com.caldi.people.common

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.common.states.PersonProfileViewState
import com.caldi.factories.PeopleViewModelFactory
import com.caldi.people.common.personprofile.PersonProfileFragment
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

abstract class PeopleActivity : BaseDrawerActivity(), PeopleView {

    @Inject
    lateinit var peopleViewModelFactory: PeopleViewModelFactory

    lateinit var peopleViewModel: PeopleViewModel

    enum class ExitAnimDirection { LEFT, RIGHT, UP }

    lateinit var positiveMeetSubject: Subject<String>
    lateinit var negativeMeetSubject: Subject<String>

    protected lateinit var profilesFetchingSubject: Subject<String>
    private lateinit var questionsFetchingSubject: Subject<Boolean>

    private var initialFetch = true

    private var recentlyAddedProfileId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        peopleViewModel = ViewModelProviders.of(this, peopleViewModelFactory)[PeopleViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        peopleViewModel.bind(this, eventId)
        if (initialFetch) {
            profilesFetchingSubject.onNext("")
            questionsFetchingSubject.onNext(true)
        }
    }

    private fun initEmitters() {
        positiveMeetSubject = PublishSubject.create()
        negativeMeetSubject = PublishSubject.create()
        profilesFetchingSubject = PublishSubject.create()
        questionsFetchingSubject = PublishSubject.create()
    }

    override fun onStop() {
        initialFetch = false
        peopleViewModel.unbind()
        super.onStop()
    }

    override fun emitPositiveMeet(): Observable<String> = positiveMeetSubject.doOnNext {
        removePersonProfileFragment(it, ExitAnimDirection.RIGHT)
    }

    override fun emitNegativeMeet(): Observable<String> = negativeMeetSubject.doOnNext {
        removePersonProfileFragment(it, ExitAnimDirection.LEFT)
    }

    override fun emitProfilesFetchingTrigger(): Observable<String> = profilesFetchingSubject

    override fun emitQuestionsFetchingTrigger(): Observable<Boolean> = questionsFetchingSubject

    fun addPersonProfileFragment(personProfileViewState: PersonProfileViewState) {
        recentlyAddedProfileId = personProfileViewState.userId
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .setCustomAnimations(R.anim.up_enter, 0)
                .add(R.id.fragmentsContainer, PersonProfileFragment.newInstance(personProfileViewState), personProfileViewState.userId)
        fragmentTransaction.commit()
    }

    open fun removePersonProfileFragment(userId: String = recentlyAddedProfileId, exitAnimDirection: ExitAnimDirection) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        when (exitAnimDirection) {
            ExitAnimDirection.LEFT -> {
                fragmentTransaction.setCustomAnimations(0, R.anim.left_exit)
            }
            ExitAnimDirection.RIGHT -> {
                fragmentTransaction.setCustomAnimations(0, R.anim.right_exit)
            }
            ExitAnimDirection.UP -> {
                fragmentTransaction.setCustomAnimations(0, R.anim.up_exit)
            }
        }

        fragmentTransaction.remove(supportFragmentManager.findFragmentByTag(userId))
        fragmentTransaction.commit()
    }
}