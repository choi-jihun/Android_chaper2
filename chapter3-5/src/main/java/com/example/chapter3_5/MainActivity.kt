package com.example.chapter3_5

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chapter3_5.databinding.ActivityMainBinding
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import org.jsoup.Jsoup
import retrofit2.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val retrofit =
        Retrofit.Builder().baseUrl("https://news.google.com/").addConverterFactory(
            TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build())
        ).build()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsAdapter = NewsAdapter{
            val intent = Intent(this,WebViewActivity::class.java).putExtra("url",it)
            startActivity(intent)
        }
        val newsService = retrofit.create(NewsService::class.java)

        newsService.mainFeed().submitList()

        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = newsAdapter
        }

        binding.feedsChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.feedsChip.isChecked = true
            newsService.mainFeed().submitList()
        }

        binding.politicsChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.politicsChip.isChecked = true
            newsService.politicsNews().submitList()
        }

        binding.economyChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.economyChip.isChecked = true
            newsService.economyNews().submitList()
        }

        binding.socialChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.socialChip.isChecked = true
            newsService.socialNews().submitList()
        }

        binding.itChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.itChip.isChecked = true
            newsService.itNews().submitList()
        }

        binding.sportsChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.sportsChip.isChecked = true
            newsService.sportNews().submitList()
        }

        binding.searchText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                binding.chipGroup.clearCheck()
                binding.searchText.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken,0) // 검색 시 키보드 내리기
                newsService.search(binding.searchText.text.toString()).submitList()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

    }

    private fun Call<NewsRss>.submitList() {
        enqueue(object : Callback<NewsRss> {
            override fun onResponse(call: Call<NewsRss>, response: Response<NewsRss>) {
                val list = response.body()?.channel?.items.orEmpty()
                    .transform() //orEmpty() -> 변수가 null이면 빈 값 삽입
                newsAdapter.submitList(list)

                binding.notFound.isVisible = list.isEmpty()

                list.forEachIndexed { index, news ->
                    Thread {
                        try {
                            val jsoup = Jsoup.connect(news.link).get()
                            val elements = jsoup.select("meta[property^=og:]")
                            val ogImageNode = elements.find { node ->
                                node.attr("property") == "og:image"
                            }
                            news.imgUrl = ogImageNode?.attr("content")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        runOnUiThread {
                            newsAdapter.notifyItemChanged(index)
                        }

                    }.start()
                }

            }

            override fun onFailure(call: Call<NewsRss>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}