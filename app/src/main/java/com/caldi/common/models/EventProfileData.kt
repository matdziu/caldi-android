package com.caldi.common.models

data class EventProfileData(val eventUserName: String = "",
                            val answers: Map<String, String> = mapOf(),
                            val profilePictureUrl: String = "",
                            val userLinkUrl: String = "")