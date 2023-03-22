package com.example.chapter3_11

data class Home(
    val user: User,
    val appbarImage: String,
    val banner: Banner
)

data class User(
    val nickname: String,
    val starCount: Int,
    val totalCount: Int
)

data class Banner(
    val image: String,
    val contentDescription: String
)