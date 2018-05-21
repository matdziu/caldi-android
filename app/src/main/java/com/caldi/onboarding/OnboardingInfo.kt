package com.caldi.onboarding

import android.view.View

data class OnboardingInfo(val targetView: View,
                          val onboardingText: String,
                          val onboardingKey: String)