package com.caldi.base

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.caldi.R
import com.caldi.login.LoginActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_opened, R.string.drawer_closed)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.white)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return if (!item.isChecked) {
            when (item.itemId) {
                R.id.sign_out_item -> signOut()
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
        finish()
    }

    fun setNavigationSelection(menuItemId: Int) {
        navigationView.setCheckedItem(menuItemId)
    }
}