package com.example.chapter3_11

data class Menu(
    val coffee: List<MenuItem>,
    val food : List<MenuItem>
)

data class MenuItem(
    val image: String,
    val name: String
)