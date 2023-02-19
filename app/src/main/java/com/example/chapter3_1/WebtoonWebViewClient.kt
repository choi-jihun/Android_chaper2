package com.example.chapter3_1

import android.graphics.Bitmap
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.core.view.isVisible

//()-> Unit 함수를 받는 방법 매개변수X 리턴값X

class WebtoonWebViewClient(private val progressBar: ProgressBar, private val saveData: (String)-> Unit) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if(request!=null && request.url.toString().contains("comic.naver.com")){
            saveData(request.url.toString()) //invoke 생략가능
        }
//        if(request != null && request.url.toString().contains("comic.naver.com"))
//            return false
//        else return true
        //false 반환 시 정상 진행 true 반환 시 진행 안됨
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        progressBar.isVisible = false
        //progressBar.visibility = View.GONE 두개중에 아무거나
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        progressBar.visibility = View.VISIBLE
    }

}