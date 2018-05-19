package com.caldi.chatlist.utils

import com.caldi.chatlist.models.ChatItem
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ChatItemChangedListener(private val chatItemHandler: (ChatItem) -> Unit) : ChildEventListener {

    override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
        dataSnapshot.getValue(ChatItem::class.java)?.let { chatItemHandler(it) }
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