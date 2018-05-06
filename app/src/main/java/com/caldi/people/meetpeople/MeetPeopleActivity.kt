package com.caldi.people.meetpeople

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.caldi.R
import com.caldi.common.states.PersonProfileViewState
import com.caldi.people.common.PeopleActivity
import com.caldi.people.common.PeopleViewState
import com.caldi.people.filterpeople.FilterPeopleActivity
import kotlinx.android.synthetic.main.activity_meet_people.noPeopleToMeetTextView
import kotlinx.android.synthetic.main.activity_meet_people.progressBar

class MeetPeopleActivity : PeopleActivity() {

    private var fetchProfilesOnStart = true

    private var currentProfilesViewStates = listOf<PersonProfileViewState>()
    private var currentProfilesBatchSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_meet_people)
        super.onCreate(savedInstanceState)
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
        peopleViewModel.bind(this, eventId)
        if (fetchProfilesOnStart) {
            profilesFetchingSubject.onNext(true)
        }
    }

    override fun onStop() {
        fetchProfilesOnStart = false
        peopleViewModel.unbind()
        super.onStop()
    }

    override fun render(peopleViewState: PeopleViewState) {
        with(peopleViewState) {
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
            profilesFetchingSubject.onNext(true)
        }
        super.removePersonProfileFragment(userId, exitAnimDirection)
    }
}