package com.caldi.settings

import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import com.caldi.R
import com.caldi.constants.EVENT_CHAT_MESSAGES_KEY_PREFIX
import com.caldi.constants.EVENT_NEW_CONNECTIONS_KEY_PREFIX
import com.caldi.constants.EVENT_ORGANIZER_MESSAGES_KEY_PREFIX

class EventSettingsFragment : PreferenceFragment() {

    private lateinit var hostingActivity: EventSettingsActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings_container)

        hostingActivity = activity as EventSettingsActivity

        with(preferenceScreen) {
            val notificationsCategory = PreferenceCategory(context)
            notificationsCategory.title = getString(R.string.event_notifications_preference_category)

            val organizerMessagesPreference = CheckBoxPreference(context)
            organizerMessagesPreference.title = getString(R.string.organizer_channel_title)
            organizerMessagesPreference.setDefaultValue(true)
            organizerMessagesPreference.key = "$EVENT_ORGANIZER_MESSAGES_KEY_PREFIX${hostingActivity.eventId}"

            val chatMessagesPreference = CheckBoxPreference(context)
            chatMessagesPreference.title = getString(R.string.chat_message_channel_title)
            chatMessagesPreference.setDefaultValue(true)
            chatMessagesPreference.key = "$EVENT_CHAT_MESSAGES_KEY_PREFIX${hostingActivity.eventId}"

            val newConnectionsPreference = CheckBoxPreference(context)
            newConnectionsPreference.title = getString(R.string.new_connection_channel_title)
            newConnectionsPreference.setDefaultValue(true)
            newConnectionsPreference.key = "$EVENT_NEW_CONNECTIONS_KEY_PREFIX${hostingActivity.eventId}"

            addPreference(notificationsCategory)
            addPreference(organizerMessagesPreference)
            addPreference(chatMessagesPreference)
            addPreference(newConnectionsPreference)
        }
    }
}