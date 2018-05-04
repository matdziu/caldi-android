package com.caldi.filterpeople

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.factories.FilterPeopleViewModelFactory
import com.caldi.filterpeople.models.spinner.NameSpinnerItem
import com.caldi.filterpeople.spinner.FilterSpinnerAdapter
import com.caldi.meetpeople.MeetPeopleActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_filter_people.filterSpinner
import javax.inject.Inject

class FilterPeopleActivity : BaseDrawerActivity(), FilterPeopleView {

    @Inject
    lateinit var filterPeopleViewModelFactory: FilterPeopleViewModelFactory

    private lateinit var filterPeopleViewModel: FilterPeopleViewModel

    private lateinit var filterSpinnerAdapter: FilterSpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_filter_people)
        super.onCreate(savedInstanceState)
        initSpinner()

        filterPeopleViewModel = ViewModelProviders.of(this, filterPeopleViewModelFactory)[FilterPeopleViewModel::class.java]

        filterSpinnerAdapter.setFilterSpinnerItemList(listOf(
                NameSpinnerItem("Attendee name"),
                NameSpinnerItem("Posted link (optional)"),
                NameSpinnerItem("What is your favourite drink?"),
                NameSpinnerItem("What is your beloved music?")
        ))
    }

    private fun initSpinner() {
        filterSpinnerAdapter = FilterSpinnerAdapter(this, android.R.layout.simple_spinner_item)
        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = filterSpinnerAdapter
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedFilterSpinnerItem = filterSpinnerAdapter.getItem(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // unused
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_people_view, menu)
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

    }

    override fun onStop() {
        filterPeopleViewModel.unbind()
        super.onStop()
    }

    override fun render(filterPeopleViewState: FilterPeopleViewState) {

    }
}