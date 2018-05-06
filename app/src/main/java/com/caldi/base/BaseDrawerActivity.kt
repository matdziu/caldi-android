package com.caldi.base

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.caldi.R
import com.caldi.chatlist.ChatListActivity
import com.caldi.eventprofile.EventProfileActivity
import com.caldi.home.HomeActivity
import com.caldi.login.LoginActivity
import com.caldi.meetpeople.MeetPeopleActivity
import com.caldi.organizer.OrganizerActivity
import com.google.firebase.auth.FirebaseAuth

open class BaseDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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

    companion object {
        var eventId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                R.id.events_item -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.event_profile_item -> startActivity(Intent(this, EventProfileActivity::class.java))
                R.id.meet_people_item -> startActivity(Intent(this, MeetPeopleActivity::class.java))
                R.id.chat_item -> startActivity(Intent(this, ChatListActivity::class.java))
                R.id.organizer_item -> OrganizerActivity.start(this, eventId)
            }
            drawerLayout.closeDrawers()
            true
        } else {
            false
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
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