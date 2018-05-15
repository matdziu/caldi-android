package com.caldi.settings

import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import com.caldi.R
import com.caldi.constants.GENERAL_CHAT_MESSAGES_KEY
import com.caldi.constants.GENERAL_NEW_CONNECTIONS_KEY
import com.caldi.constants.GENERAL_ORGANIZER_MESSAGES_KEY

class GeneralSettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings_container)

        with(preferenceScreen) {
            val notificationsCategory = PreferenceCategory(context)
            notificationsCategory.title = getString(R.string.notifications_preference_category)

            val organizerMessagesPreference = CheckBoxPreference(context)
            organizerMessagesPreference.title = getString(R.string.organizer_channel_title)
            organizerMessagesPreference.setDefaultValue(true)
            organizerMessagesPreference.key = GENERAL_ORGANIZER_MESSAGES_KEY

            val chatMessagesPreference = CheckBoxPreference(context)
            chatMessagesPreference.title = getString(R.string.chat_message_channel_title)
            chatMessagesPreference.setDefaultValue(true)
            chatMessagesPreference.key = GENERAL_CHAT_MESSAGES_KEY

            val newConnectionsPreference = CheckBoxPreference(context)
            newConnectionsPreference.title = getString(R.string.new_connection_channel_title)
            newConnectionsPreference.setDefaultValue(true)
            newConnectionsPreference.key = GENERAL_NEW_CONNECTIONS_KEY

            addPreference(notificationsCategory)
            addPreference(organizerMessagesPreference)
            addPreference(chatMessagesPreference)
            addPreference(newConnectionsPreference)
        }
    }
}