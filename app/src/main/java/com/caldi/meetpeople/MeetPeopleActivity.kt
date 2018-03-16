package com.caldi.meetpeople

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.EVENT_ID_KEY
import com.caldi.factories.MeetPeopleViewModelFactory
import com.caldi.meetpeople.personprofile.PersonProfileFragment
import com.caldi.meetpeople.personprofile.PersonProfileViewState
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class MeetPeopleActivity : BaseDrawerActivity(), MeetPeopleView {

    enum class ExitAnimDirection { LEFT, RIGHT }

    val dismissProfileSubject: Subject<String> = PublishSubject.create()
    val acceptProfileSubject: Subject<String> = PublishSubject.create()

    private val triggerProfilesFetchingSubject: Subject<String> = PublishSubject.create()

    private var fetchProfiles = true

    @Inject
    lateinit var meetPeopleViewModelFactory: MeetPeopleViewModelFactory

    private lateinit var meetPeopleViewModel: MeetPeopleViewModel

    companion object {

        fun start(context: Context, eventId: String) {
            val intent = Intent(context, MeetPeopleActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_meet_people)
        super.onCreate(savedInstanceState)

        eventId = intent.getStringExtra(EVENT_ID_KEY)

        meetPeopleViewModel = ViewModelProviders.of(this, meetPeopleViewModelFactory)[MeetPeopleViewModel::class.java]

        dismissProfileSubject.subscribe { removePersonProfileFragment(it, ExitAnimDirection.LEFT) }
        acceptProfileSubject.subscribe { removePersonProfileFragment(it, ExitAnimDirection.RIGHT) }
    }

    override fun onStart() {
        super.onStart()
        setNavigationSelection(R.id.meet_people_item)
        meetPeopleViewModel.bind(this)
        if (fetchProfiles) triggerProfilesFetchingSubject.onNext(eventId)
    }

    override fun onStop() {
        fetchProfiles = false
        meetPeopleViewModel.unbind()
        super.onStop()
    }

    override fun emitProfilesFetchingTrigger(): Observable<String> = triggerProfilesFetchingSubject

    override fun render(meetPeopleViewState: MeetPeopleViewState) {
        Log.d("mateusz", meetPeopleViewState.toString())
    }

    private fun addPersonProfileFragment(tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .setCustomAnimations(R.anim.up_enter, 0)
                .add(R.id.fragmentsContainer, PersonProfileFragment.newInstance(PersonProfileViewState()), tag)
        fragmentTransaction.commit()
    }

    private fun removePersonProfileFragment(tag: String, exitAnimDirection: ExitAnimDirection) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        when (exitAnimDirection) {
            ExitAnimDirection.LEFT -> fragmentTransaction.setCustomAnimations(0, R.anim.left_exit)
            ExitAnimDirection.RIGHT -> fragmentTransaction.setCustomAnimations(0, R.anim.right_exit)
        }

        fragmentTransaction.remove(supportFragmentManager.findFragmentByTag(tag))
        fragmentTransaction.commit()
    }
}