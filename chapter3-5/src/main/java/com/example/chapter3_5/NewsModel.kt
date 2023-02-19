package com.example.chapter3_5

data class NewsModel(
    val title: String,
    val link: String,
    var imgUrl: String ?= null
)

fun List<NewsItem>.transform() : List<NewsModel> {
    return this.map {
        NewsModel(
            title = it.title ?: "",
            link = it.link ?: "",
            imgUrl = null
        )
    }
}