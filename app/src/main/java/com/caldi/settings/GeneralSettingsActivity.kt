package com.caldi.settings

import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseOverflowActivity
import com.caldi.onboarding.OnboardingInfo
import kotlinx.android.synthetic.main.toolbar.logo

class GeneralSettingsActivity : BaseOverflowActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_general_settings)
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentsContainer, GeneralSettingsFragment())
                .commit()
        showOnboarding(
                OnboardingInfo(logo, getString(R.string.onboarding_general_settings), "generalSettingsOnboarding")
        )
    }
}