package com.caldi.settings

import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseOverflowActivity

class GeneralSettingsActivity : BaseOverflowActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_general_settings)
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentsContainer, GeneralSettingsFragment())
                .commit()
    }
}