package com.caldi.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.EVENT_ID_KEY
import com.caldi.onboarding.OnboardingInfo
import kotlinx.android.synthetic.main.toolbar.logo

class EventSettingsActivity : BaseDrawerActivity() {

    companion object {

        fun start(context: Context,
                  eventId: String) {
            val intent = Intent(context, EventSettingsActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_event_settings)
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentsContainer, EventSettingsFragment())
                .commit()
        showOnboarding(
                OnboardingInfo(logo, getString(R.string.onboarding_event_settings), "eventSettingsOnboarding")
        )
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.event_settings_item)
    }
}