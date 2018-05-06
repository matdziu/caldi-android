package com.caldi.people.meetpeople.list

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class AnswerViewState(val question: String, val answer: String) : Parcelable