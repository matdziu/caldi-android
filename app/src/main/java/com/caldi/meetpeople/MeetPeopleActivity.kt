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

    enum class ExitAnimDirection { LEFT, RIGHT }

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

        dismissProfileSubject.subscribe { removePersonProfileFragment(it, ExitAnimDirection.LEFT) }
        acceptProfileSubject.subscribe { removePersonProfileFragment(it, ExitAnimDirection.RIGHT) }

        addPersonProfileFragment("1")
        addPersonProfileFragment("2")
        addPersonProfileFragment("3")
        addPersonProfileFragment("4")
        addPersonProfileFragment("5")
    }

    private fun addPersonProfileFragment(tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .setCustomAnimations(R.anim.up_enter, 0)
                .add(R.id.fragmentsContainer, PersonProfileFragment.newInstance(), tag)
        fragmentTransaction.commit()
    }

    private fun removePersonProfileFragment(tag: String, exitAnimDirection: ExitAnimDirection) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        when (exitAnimDirection) {
            ExitAnimDirection.LEFT -> fragmentTransaction.setCustomAnimations(0, R.anim.left_exit)
            ExitAnimDirection.RIGHT -> fragmentTransaction.setCustomAnimations(0, R.anim.right_exit)
        }

        fragmentTransaction.remove(supportFragmentManager.findFragmentByTag(tag))
        fragmentTransaction.commit()
    }

    override fun onStart() {
        super.onStart()
        setNavigationSelection(R.id.meet_people_item)
    }
}