package com.caldi.organizer

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class OrganizerInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
}