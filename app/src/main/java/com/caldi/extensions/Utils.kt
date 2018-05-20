package com.caldi.extensions

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentISODate(): String {
    val timeZone = TimeZone.getTimeZone("UTC")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
    dateFormat.timeZone = timeZone
    return dateFormat.format(Date())
}