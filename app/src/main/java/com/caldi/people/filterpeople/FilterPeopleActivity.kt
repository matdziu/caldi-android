package com.caldi.people.filterpeople

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.caldi.R
import com.caldi.common.states.PersonProfileViewState
import com.caldi.people.common.PeopleActivity
import com.caldi.people.common.PeopleViewState
import com.caldi.people.filterpeople.list.PersonProfilesAdapter
import com.caldi.people.filterpeople.spinner.FilterSpinnerAdapter
import com.caldi.people.filterpeople.spinner.FilterType.LinkFilterType
import com.caldi.people.filterpeople.spinner.FilterType.NameFilterType
import com.caldi.people.filterpeople.spinner.FilterType.QuestionFilterType
import com.caldi.people.meetpeople.MeetPeopleActivity
import com.caldi.people.meetpeople.list.AnswerViewState
import kotlinx.android.synthetic.main.activity_filter_people.filterSpinner
import kotlinx.android.synthetic.main.activity_filter_people.peopleRecyclerView

class FilterPeopleActivity : PeopleActivity() {

    private lateinit var filterSpinnerAdapter: FilterSpinnerAdapter

    private val personProfilesAdapter = PersonProfilesAdapter(this)

    private var isBatchLoading = false

    var viewPersonProfileMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_filter_people)
        super.onCreate(savedInstanceState)
        initSpinner()
        initRecyclerView()

        filterSpinnerAdapter.setFilterTypeList(listOf(
                NameFilterType("Attendee name"),
                LinkFilterType("Posted link (optional)"),
                QuestionFilterType("What's your favourite drink"),
                QuestionFilterType("What's your favourite song")
        ))

        personProfilesAdapter.submitList(listOf(
                PersonProfileViewState("abcdefgh", "Matt, the Android Dude", "https://themify.me/demo/themes/pinshop/files/2012/12/man-in-suit2.jpg", "medium.com/@matdziu", listOf(
                        AnswerViewState("What's your favourite drink", "Beer"),
                        AnswerViewState("What's your favourite song", "Stairway to heaven")
                ))
        ))
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
                    profilesFetchingSubject.onNext(true)
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

    }

    fun enableViewPersonProfileMode(enable: Boolean) {
        showBackToolbarArrow(enable, { onBackPressed() })
        viewPersonProfileMode = enable
        invalidateOptionsMenu()
    }

    override fun onBackPressed() {
        if (!viewPersonProfileMode) {
            super.onBackPressed()
        } else {
            enableViewPersonProfileMode(false)
            removePersonProfileFragment(exitAnimDirection = ExitAnimDirection.UP)
        }
    }
}