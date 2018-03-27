package com.caldi.chat.list

data class MessageViewState(val message: String = "",
                            val messageId: String = "",
                            val isOwn: Boolean = true)