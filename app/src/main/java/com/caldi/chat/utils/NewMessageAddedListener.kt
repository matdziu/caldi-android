package com.caldi.chat.utils

import com.caldi.chat.models.Message
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

abstract class NewMessageAddedListener : ChildEventListener {

    abstract fun onNewMessageAdded(newMessage: Message)

    override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
        dataSnapshot.getValue(Message::class.java)?.let { if (it.isNotEmpty()) onNewMessageAdded(it) }
    }

    override fun onCancelled(p0: DatabaseError?) {
        // unused
    }

    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
        // unused
    }

    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
        // unused
    }

    override fun onChildRemoved(p0: DataSnapshot?) {
        // unused
    }
}