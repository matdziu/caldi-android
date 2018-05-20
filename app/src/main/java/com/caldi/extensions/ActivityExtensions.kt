package com.caldi.extensions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject


fun Activity.hideSoftKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Activity.checkIfOnline(): Subject<Boolean> {
    val resultSubject = PublishSubject.create<Boolean>()
    val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
    connectedRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.getValue(Boolean::class.java)?.let { resultSubject.onNext(it) }
        }

        override fun onCancelled(error: DatabaseError) {
            resultSubject.onNext(false)
        }
    })
    return resultSubject
}
