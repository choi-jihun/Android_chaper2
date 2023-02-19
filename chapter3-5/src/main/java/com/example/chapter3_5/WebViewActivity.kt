package com.example.chapter3_5

import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter3_5.databinding.ActivitiyWebviewBinding

class WebViewActivity: AppCompatActivity() {

    private lateinit var binding: ActivitiyWebviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitiyWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("url")

        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        if(url.isNullOrEmpty()){
            Toast.makeText(this,"잘못된 URL입니다.",Toast.LENGTH_SHORT).show()
            finish()
        }

        else
            binding.webView.loadUrl(url)
    }
}