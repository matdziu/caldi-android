package com.caldi.common.models

data class EventProfileData(var userId: String = "",
                            val eventUserName: String = "",
                            val answers: Map<String, String> = mapOf(),
                            val profilePicture: String = "",
                            val userLinkUrl: String = "")