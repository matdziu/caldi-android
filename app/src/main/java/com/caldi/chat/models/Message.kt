package com.caldi.chat.models

data class Message(val timestamp: Long = 0, val message: String = "", val senderId: String = "") {

    fun isNotEmpty(): Boolean {
        return timestamp != 0L
                && message.isNotEmpty()
                && senderId.isNotEmpty()
    }
}