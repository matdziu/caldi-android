package com.caldi.chat

data class ChatViewState(val progress: Boolean = false,
                         val error: Boolean = false,
                         val dismissToast: Boolean = false)