package com.caldi.chat.models

data class Message(val timestamp: Long = 0, val message: String = "", val senderId: String = "")