package com.caldi.filterpeople

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.common.states.PersonProfileViewState
import com.caldi.factories.FilterPeopleViewModelFactory
import com.caldi.filterpeople.list.PersonProfilesAdapter
import com.caldi.filterpeople.spinner.FilterSpinnerAdapter
import com.caldi.filterpeople.spinner.FilterType.LinkFilterType
import com.caldi.filterpeople.spinner.FilterType.NameFilterType
import com.caldi.filterpeople.spinner.FilterType.QuestionFilterType
import com.caldi.meetpeople.MeetPeopleActivity
import com.caldi.meetpeople.list.AnswerViewState
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_filter_people.filterSpinner
import kotlinx.android.synthetic.main.activity_filter_people.peopleRecyclerView
import javax.inject.Inject

class FilterPeopleActivity : BaseDrawerActivity(), FilterPeopleView {

    @Inject
    lateinit var filterPeopleViewModelFactory: FilterPeopleViewModelFactory

    private lateinit var filterPeopleViewModel: FilterPeopleViewModel

    private lateinit var filterSpinnerAdapter: FilterSpinnerAdapter

    private val personProfilesAdapter = PersonProfilesAdapter()

    private lateinit var profilesFetchingSubject: Subject<Boolean>

    private var isBatchLoading = false

    private var showChangePeopleViewButton = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_filter_people)
        super.onCreate(savedInstanceState)
        initSpinner()
        initRecyclerView()

        filterPeopleViewModel = ViewModelProviders.of(this, filterPeopleViewModelFactory)[FilterPeopleViewModel::class.java]

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

                if (position == 1) {
                    toggleBackArrow(false)
                    showChangePeopleViewButton = true
                    invalidateOptionsMenu()
                }
                if (position == 2) {
                    toggleBackArrow(true)
                    showChangePeopleViewButton = false
                    invalidateOptionsMenu()
                }
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
        if (showChangePeopleViewButton) {
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

    override fun onStart() {
        super.onStart()
        initEmitters()
//        filterPeopleViewModel.bind(this)
    }

    private fun initEmitters() {
        profilesFetchingSubject = PublishSubject.create()
    }

    override fun onStop() {
        filterPeopleViewModel.unbind()
        super.onStop()
    }

    override fun emitProfilesFetchingTrigger(): Observable<Boolean> = profilesFetchingSubject

    override fun render(filterPeopleViewState: FilterPeopleViewState) {

    }
}