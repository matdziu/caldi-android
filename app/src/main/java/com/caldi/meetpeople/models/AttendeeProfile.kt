package com.caldi.meetpeople.models

data class AttendeeProfile(val userId: String = "",
                           val eventUserName: String = "",
                           val userLinkUrl: String = "",
                           val profilePictureUrl: String = "",
                           val answers: Map<String, String> = mapOf(),
                           var questions: Map<String, String> = mapOf())