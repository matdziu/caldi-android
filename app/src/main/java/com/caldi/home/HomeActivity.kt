package com.caldi.home

import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseDrawerActivity

class HomeActivity : BaseDrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_home)
        super.onCreate(savedInstanceState)
    }
}