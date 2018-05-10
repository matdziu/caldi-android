package com.caldi.chatlist.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class ChatItem(val chatId: String = "",
                    val name: String = "",
                    val imageUrl: String = "",
                    val receiverId: String = "") : Parcelable