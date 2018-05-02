package com.caldi.common.models

data class EventProfileData(val eventUserName: String = "",
                            val answers: Map<String, String> = mapOf(),
                            val profilePicture: String = "",
                            val userLinkUrl: String = "")