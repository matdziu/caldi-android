package com.caldi.people.filterpeople

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.caldi.R
import com.caldi.common.states.PersonProfileViewState
import com.caldi.people.common.PeopleActivity
import com.caldi.people.common.PeopleViewState
import com.caldi.people.filterpeople.list.PersonProfilesAdapter
import com.caldi.people.filterpeople.spinner.FilterSpinnerAdapter
import com.caldi.people.filterpeople.spinner.FilterType
import com.caldi.people.filterpeople.spinner.FilterType.LinkFilterType
import com.caldi.people.filterpeople.spinner.FilterType.NameFilterType
import com.caldi.people.filterpeople.spinner.FilterType.QuestionFilterType
import com.caldi.people.meetpeople.MeetPeopleActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_filter_people.filterSpinner
import kotlinx.android.synthetic.main.activity_filter_people.peopleRecyclerView
import kotlinx.android.synthetic.main.activity_filter_people.progressBar

class FilterPeopleActivity : PeopleActivity() {

    private lateinit var filterSpinnerAdapter: FilterSpinnerAdapter

    private val personProfilesAdapter = PersonProfilesAdapter(this)

    private var recentProfilesBatch = listOf<PersonProfileViewState>()

    private var isBatchLoading = false

    var viewPersonProfileMode = false

    private var defaultFilterTypeList = listOf<FilterType>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_filter_people)
        super.onCreate(savedInstanceState)

        defaultFilterTypeList = listOf(
                NameFilterType(getString(R.string.attendee_name_filter_text)),
                LinkFilterType(getString(R.string.posted_link_filter_text))
        )

        initSpinner()
        initRecyclerView()
    }

    private fun initSpinner() {
        filterSpinnerAdapter = FilterSpinnerAdapter(this, android.R.layout.simple_spinner_item)
        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = filterSpinnerAdapter
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedFilterType = filterSpinnerAdapter.getItem(position)
                personProfilesAdapter.filterType = selectedFilterType
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // unused
            }
        }
    }

    private fun initRecyclerView() {
        peopleRecyclerView.layoutManager = LinearLayoutManager(this)
        peopleRecyclerView.adapter = personProfilesAdapter
        peopleRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) && !isBatchLoading) {
                    isBatchLoading = true
                    profilesFetchingSubject.onNext(
                            if (recentProfilesBatch.isNotEmpty()) recentProfilesBatch.first().userId else ""
                    )
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!viewPersonProfileMode) {
            menuInflater.inflate(R.menu.menu_people_view, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_people_view_item -> startActivity(Intent(this, MeetPeopleActivity::class.java))
        }
        finish()
        return false
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.meet_people_item)
    }

    override fun render(peopleViewState: PeopleViewState) {
        with(peopleViewState) {
            promptToFillEventProfile(eventProfileBlank, dismissToast)
            showProgressBar(progress)
            showError(error, dismissToast)

            filterSpinnerAdapter.setFilterTypeList(defaultFilterTypeList + convertToQuestionFilterTypes(eventQuestions))
            if (personProfileViewStateList.isNotEmpty() && recentProfilesBatch != personProfileViewStateList) {
                recentProfilesBatch = personProfileViewStateList
                personProfilesAdapter.addProfilesBatch(personProfileViewStateList)
                isBatchLoading = false
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
        } else {
            progressBar.visibility = View.GONE
        }
    }

    private fun showError(show: Boolean, dismissToast: Boolean = false) {
        if (show && !dismissToast) {
            Toast.makeText(this, getString(R.string.error_event_profile_text), Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertToQuestionFilterTypes(questions: Map<String, String>): List<QuestionFilterType> {
        return questions.values.map { QuestionFilterType(it) }
    }

    fun enableViewPersonProfileMode(enable: Boolean) {
        showBackToolbarArrow(enable, { onBackPressed() })
        viewPersonProfileMode = enable
        invalidateOptionsMenu()
    }

    override fun emitPositiveMeet(): Observable<String> {
        return super.emitPositiveMeet().doOnNext { sideExitViewPersonProfileMode(it) }
    }

    override fun emitNegativeMeet(): Observable<String> {
        return super.emitNegativeMeet().doOnNext { sideExitViewPersonProfileMode(it) }
    }

    private fun sideExitViewPersonProfileMode(profileId: String) {
        enableViewPersonProfileMode(false)
        personProfilesAdapter.removeProfileFromList(profileId)
    }

    private fun upExitViewPersonProfileMode() {
        enableViewPersonProfileMode(false)
        removePersonProfileFragment(exitAnimDirection = ExitAnimDirection.UP)
    }

    override fun onBackPressed() {
        if (!viewPersonProfileMode) {
            super.onBackPressed()
        } else {
            upExitViewPersonProfileMode()
        }
    }
}