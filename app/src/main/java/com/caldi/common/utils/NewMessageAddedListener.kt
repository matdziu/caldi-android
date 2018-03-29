package com.caldi.common.utils

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

abstract class NewMessageAddedListener<in T>(private val clazz: Class<T>) : ChildEventListener {

    abstract fun onNewMessageAdded(newMessage: T)

    override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
        dataSnapshot.getValue(clazz)?.let { onNewMessageAdded(it) }
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