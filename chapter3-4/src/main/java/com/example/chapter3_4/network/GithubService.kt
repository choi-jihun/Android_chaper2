package com.example.chapter3_4.network

import com.example.chapter3_4.model.Repo
import com.example.chapter3_4.model.UserDto
import retrofit2.Call
import retrofit2.http.*

interface GithubService {
//    @Headers("Authorization: Bearer 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")

    @GET("/users/{username}/repos")
    fun listRepos(@Path("username") username: String, @Query("page") page: Int): Call<List<Repo>>

    @GET("search/users")
    fun searchUsers(@Query("q") query: String): Call<UserDto>

}