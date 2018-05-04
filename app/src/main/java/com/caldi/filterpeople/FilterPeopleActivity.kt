package com.caldi.filterpeople

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.factories.FilterPeopleViewModelFactory
import com.caldi.meetpeople.MeetPeopleActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class FilterPeopleActivity : BaseDrawerActivity(), FilterPeopleView {

    @Inject
    lateinit var filterPeopleViewModelFactory: FilterPeopleViewModelFactory

    private lateinit var filterPeopleViewModel: FilterPeopleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_filter_people)
        super.onCreate(savedInstanceState)

        filterPeopleViewModel = ViewModelProviders.of(this, filterPeopleViewModelFactory)[FilterPeopleViewModel::class.java]
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