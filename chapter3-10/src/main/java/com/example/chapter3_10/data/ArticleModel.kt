package com.example.chapter3_10.data

data class ArticleModel(
    val articleId: String ?= null,
    val createAt: Long ?= null,
    val description: String ?= null,
    val imageUrl: String ?= null,
)
