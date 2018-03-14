package com.caldi.meetpeople

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.EVENT_ID_KEY
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class MeetPeopleActivity : BaseDrawerActivity() {

    val dismissProfileSubject: Subject<String> = PublishSubject.create()
    val acceptProfileSubject: Subject<String> = PublishSubject.create()

    companion object {

        fun start(context: Context, eventId: String) {
            val intent = Intent(context, MeetPeopleActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_meet_people)
        super.onCreate(savedInstanceState)

        eventId = intent.getStringExtra(EVENT_ID_KEY)

        dismissProfileSubject.subscribe { removePersonProfileFragment(it) }
        acceptProfileSubject.subscribe { removePersonProfileFragment(it) }

        addPersonProfileFragment("1")
        addPersonProfileFragment("2")
        addPersonProfileFragment("3")
        addPersonProfileFragment("4")
        addPersonProfileFragment("5")
    }

    private fun addPersonProfileFragment(tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentsContainer, PersonProfileFragment.newInstance(), tag)
        fragmentTransaction.commit()
    }

    private fun removePersonProfileFragment(tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.remove(supportFragmentManager.findFragmentByTag(tag))
        fragmentTransaction.commit()
    }

    override fun onStart() {
        super.onStart()
        setNavigationSelection(R.id.meet_people_item)
    }
}