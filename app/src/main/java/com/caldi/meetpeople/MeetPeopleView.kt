package com.caldi.meetpeople

import com.caldi.base.BasePeopleView

interface MeetPeopleView : BasePeopleView {

    fun render(meetPeopleViewState: MeetPeopleViewState)
}