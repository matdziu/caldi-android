package com.caldi.meetpeople.personprofile

import android.annotation.SuppressLint
import android.os.Parcelable
import com.caldi.meetpeople.list.AnswerViewState
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class PersonProfileViewState(val eventUserName: String = "",
                                  val profilePictureUrl: String = "",
                                  val answerViewStateList: List<AnswerViewState> = listOf()) : Parcelable