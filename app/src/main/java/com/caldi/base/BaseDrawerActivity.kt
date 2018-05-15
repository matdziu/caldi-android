package com.caldi.base

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import com.caldi.R
import com.caldi.chatlist.ChatListActivity
import com.caldi.constants.EVENT_ID_KEY
import com.caldi.eventprofile.EventProfileActivity
import com.caldi.home.HomeActivity
import com.caldi.login.LoginActivity
import com.caldi.organizer.OrganizerActivity
import com.caldi.people.meetpeople.MeetPeopleActivity
import com.caldi.settings.EventSettingsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId

open class BaseDrawerActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val drawerLayout: DrawerLayout by lazy {
        findViewById<DrawerLayout>(R.id.drawerLayout)
    }
    private val navigationView: NavigationView by lazy {
        findViewById<NavigationView>(R.id.navigationView)
    }
    private val toolbar: Toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar)
    }

    private lateinit var toggle: ActionBarDrawerToggle

    var eventId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventId = intent.getStringExtra(EVENT_ID_KEY)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_opened, R.string.drawer_closed)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return if (!item.isChecked) {
            when (item.itemId) {
                R.id.sign_out_item -> signOut()
                R.id.events_item -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                R.id.event_profile_item -> EventProfileActivity.start(this, eventId)
                R.id.meet_people_item -> MeetPeopleActivity.start(this, eventId)
                R.id.chat_item -> ChatListActivity.start(this, eventId)
                R.id.organizer_item -> OrganizerActivity.start(this, eventId)
                R.id.event_settings_item -> EventSettingsActivity.start(this, eventId)
            }
            drawerLayout.closeDrawers()
            true
        } else {
            false
        }
    }

    private fun signOut() {
        if (online) {
            Thread { FirebaseInstanceId.getInstance().deleteInstanceId() }.start()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            Toast.makeText(this, getString(R.string.logout_connection_prompt), Toast.LENGTH_SHORT).show()
        }
    }

    fun setNavigationSelection(menuItemId: Int) {
        navigationView.setCheckedItem(menuItemId)
    }

    fun showBackToolbarArrow(show: Boolean, backAction: () -> Unit = {}) {
        if (show) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            toggle.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toggle.setToolbarNavigationClickListener { backAction() }
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            toggle.isDrawerIndicatorEnabled = true
        }
    }
}