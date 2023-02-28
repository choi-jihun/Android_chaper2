package com.example.chapter3_6.chatlist

data class ChatRoomItem(
    val chatRoomId: String ?= null,
    val otherUserId : String ?= null,
    val otherUserName: String ?= null,
    val lastMessage: String ?= null,
)
