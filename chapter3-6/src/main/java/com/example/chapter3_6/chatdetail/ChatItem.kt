package com.example.chapter3_6.chatdetail

data class ChatItem(
    var chatId : String ?= null,
    val userId : String ?= null,
    val message : String ?= null
)