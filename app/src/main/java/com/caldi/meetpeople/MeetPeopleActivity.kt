package com.caldi.meetpeople

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.caldi.R
import com.caldi.base.BasePeopleActivity
import com.caldi.common.states.PersonProfileViewState
import com.caldi.factories.MeetPeopleViewModelFactory
import com.caldi.filterpeople.FilterPeopleActivity
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_meet_people.noPeopleToMeetTextView
import kotlinx.android.synthetic.main.activity_meet_people.progressBar
import javax.inject.Inject

class MeetPeopleActivity : BasePeopleActivity(), MeetPeopleView {

    private lateinit var triggerProfilesFetchingSubject: Subject<Boolean>

    private var fetchProfilesOnStart = true

    private var currentProfilesViewStates = listOf<PersonProfileViewState>()
    private var currentProfilesBatchSize = 0

    @Inject
    lateinit var meetPeopleViewModelFactory: MeetPeopleViewModelFactory

    private lateinit var meetPeopleViewModel: MeetPeopleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_meet_people)
        super.onCreate(savedInstanceState)

        meetPeopleViewModel = ViewModelProviders.of(this, meetPeopleViewModelFactory)[MeetPeopleViewModel::class.java]
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        fetchProfilesOnStart = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_people_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_people_view_item -> startActivity(Intent(this, FilterPeopleActivity::class.java))
        }
        finish()
        return false
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.meet_people_item)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        meetPeopleViewModel.bind(this, eventId)
        if (fetchProfilesOnStart) {
            triggerProfilesFetchingSubject.onNext(true)
        }
    }

    private fun initEmitters() {
        triggerProfilesFetchingSubject = PublishSubject.create()
    }

    override fun onStop() {
        fetchProfilesOnStart = false
        meetPeopleViewModel.unbind()
        super.onStop()
    }

    override fun emitProfilesFetchingTrigger(): Observable<Boolean> = triggerProfilesFetchingSubject

    override fun emitPositiveMeet(): Observable<String> = positiveMeetSubject.doOnNext {
        removePersonProfileFragment(it, ExitAnimDirection.RIGHT)
    }

    override fun emitNegativeMeet(): Observable<String> = negativeMeetSubject.doOnNext {
        removePersonProfileFragment(it, ExitAnimDirection.LEFT)
    }

    override fun render(meetPeopleViewState: MeetPeopleViewState) {
        with(meetPeopleViewState) {
            promptToFillEventProfile(eventProfileBlank, dismissToast)
            showProgressBar(progress)
            showError(error, dismissToast)

            if (personProfileViewStateList.isNotEmpty() && currentProfilesViewStates != personProfileViewStateList) {
                currentProfilesViewStates = personProfileViewStateList
                currentProfilesBatchSize = personProfileViewStateList.size
                for (personProfileViewState in currentProfilesViewStates) {
                    addPersonProfileFragment(personProfileViewState)
                }
            }
        }
    }

    private fun promptToFillEventProfile(eventProfileBlank: Boolean, dismissToast: Boolean) {
        if (eventProfileBlank && !dismissToast) {
            Toast.makeText(this, getString(R.string.fill_event_profile_first_prompt), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
            noPeopleToMeetTextView.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            noPeopleToMeetTextView.visibility = View.VISIBLE
        }
    }

    private fun showError(show: Boolean, dismissToast: Boolean = false) {
        if (show && !dismissToast) {
            Toast.makeText(this, getString(R.string.error_event_profile_text), Toast.LENGTH_SHORT).show()
        }
    }

    override fun removePersonProfileFragment(userId: String, exitAnimDirection: ExitAnimDirection) {
        currentProfilesBatchSize -= 1
        if (currentProfilesBatchSize == 0) {
            triggerProfilesFetchingSubject.onNext(true)
        }
        super.removePersonProfileFragment(userId, exitAnimDirection)
    }
}