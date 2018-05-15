package com.caldi.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.caldi.R
import com.caldi.settings.GeneralSettingsActivity

open class BaseOverflowActivity : BaseActivity() {

    private val toolbar: Toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_overflow_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out_item -> signOut()
            R.id.general_settings_item -> startActivity(Intent(this, GeneralSettingsActivity::class.java))
        }
        return false
    }
}