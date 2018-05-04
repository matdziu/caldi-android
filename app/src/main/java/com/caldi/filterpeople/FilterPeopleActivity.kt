package com.caldi.filterpeople

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.factories.FilterPeopleViewModelFactory
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

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.meet_people_item)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        filterPeopleViewModel.bind(this)
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