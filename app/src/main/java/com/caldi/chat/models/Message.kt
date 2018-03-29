package com.caldi.chat.models

data class Message(val timestamp: String = "",
                   val message: String = "",
                   val senderId: String = "",
                   val messageId: String = "",
                   var isSent: Boolean = true)