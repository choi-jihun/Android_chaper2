package com.example.chapter3_4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chapter3_4.APIClient.retrofit
import com.example.chapter3_4.adapter.UserAdapter
import com.example.chapter3_4.databinding.ActivityMainBinding
import com.example.chapter3_4.model.UserDto
import com.example.chapter3_4.network.GithubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter : UserAdapter
    private var searchFor: String = ""
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAdapter = UserAdapter{
            val intent = Intent(this@MainActivity, RepoActivity::class.java)
            intent.putExtra("username",it.username)
            startActivity(intent)
        }

        binding.userRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        val runnable = Runnable {
            searchUser()
        }

        binding.searchEditText.addTextChangedListener {
            searchFor = it.toString()
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable,300)
        }

    }
    private fun searchUser(){
        val githubService = retrofit.create(GithubService::class.java)
        githubService.searchUsers(searchFor).enqueue(object :Callback<UserDto>{
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                Log.e("MainActivity","Search User : ${response.body().toString()}")
                userAdapter.submitList(response.body()?.items)
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Toast.makeText(this@MainActivity,"에러가 발생했습니다.",Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }

        })
    }
}