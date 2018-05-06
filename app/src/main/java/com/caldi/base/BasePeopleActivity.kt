package com.caldi.base

import com.caldi.R
import com.caldi.common.states.PersonProfileViewState
import com.caldi.meetpeople.personprofile.PersonProfileFragment
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

open class BasePeopleActivity : BaseDrawerActivity(), BasePeopleView {

    enum class ExitAnimDirection { LEFT, RIGHT, UP }

    lateinit var positiveMeetSubject: Subject<String>
    lateinit var negativeMeetSubject: Subject<String>
    protected lateinit var profilesFetchingSubject: Subject<Boolean>

    private var recentlyAddedProfileId: String = ""

    override fun onStart() {
        super.onStart()
        initEmitters()
    }

    private fun initEmitters() {
        positiveMeetSubject = PublishSubject.create()
        negativeMeetSubject = PublishSubject.create()
        profilesFetchingSubject = PublishSubject.create()
    }

    override fun emitPositiveMeet(): Observable<String> = positiveMeetSubject.doOnNext {
        removePersonProfileFragment(it, ExitAnimDirection.RIGHT)
    }

    override fun emitNegativeMeet(): Observable<String> = negativeMeetSubject.doOnNext {
        removePersonProfileFragment(it, ExitAnimDirection.LEFT)
    }

    override fun emitProfilesFetchingTrigger(): Observable<Boolean> = profilesFetchingSubject

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