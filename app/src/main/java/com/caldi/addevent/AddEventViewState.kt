package com.caldi.addevent

data class AddEventViewState(val inProgress: Boolean = false,
                             val error: Boolean = false,
                             val dismissToast: Boolean = false,
                             val success: Boolean = false)