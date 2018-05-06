package com.caldi.filterpeople

import com.caldi.base.BasePeopleView

interface FilterPeopleView : BasePeopleView {

    fun render(filterPeopleViewState: FilterPeopleViewState)
}