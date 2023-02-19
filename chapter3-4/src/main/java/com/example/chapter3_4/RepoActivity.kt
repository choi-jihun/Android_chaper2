package com.example.chapter3_4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chapter3_4.APIClient.retrofit
import com.example.chapter3_4.adapter.RepoAdapter
import com.example.chapter3_4.databinding.ActivityRepoBinding
import com.example.chapter3_4.model.Repo
import com.example.chapter3_4.network.GithubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RepoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepoBinding
    private lateinit var repoAdapter: RepoAdapter
    private var page = 0
    private var hasMore = true

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRepoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val username = intent.getStringExtra("username") ?: return

        binding.usernameTextView.text = username

        repoAdapter = RepoAdapter{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.htmlUrl))
            startActivity(intent)
        }
        val linearLayoutManager = LinearLayoutManager(this@RepoActivity)

        binding.repoRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = repoAdapter
        }

        binding.repoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalCOunt = linearLayoutManager.itemCount
                val lastVisiblePosition =
                    linearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition >= (totalCOunt - 1) && hasMore) {
                    page += 1
                    listRepo(username, page)
                }
            }
        })

        listRepo(username, page)
    }

    private fun listRepo(username: String, page: Int) {
        val githubService = retrofit.create(GithubService::class.java)
        githubService.listRepos(username, page).enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                Log.e("MainActivity", "List Repo : ${response.body().toString()}")
                hasMore = response.body()?.count() == 30
                repoAdapter.submitList(repoAdapter.currentList+response.body().orEmpty())
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {

            }

        })
    }
}