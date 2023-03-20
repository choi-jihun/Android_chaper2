package com.example.chapter3_10.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.chapter3_10.R
import com.example.chapter3_10.data.ArticleModel
import com.example.chapter3_10.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var articleAdapter: HomeArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        
        setUpWriteButton(view)
        setUpBookMarkButton()
        setUpRecyclerView()

        Firebase.firestore.collection("articles").get().addOnSuccessListener { result->
            val list = result.map {
                it.toObject<ArticleModel>()
            }
            articleAdapter.submitList(list)
        }
    }

    private fun setUpBookMarkButton() {
        binding.bookmarkImageButton.setOnClickListener {
            //findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBookMarkArticleFragment())
        }
    }

    private fun setUpWriteButton(view: View) {
        binding.writeButton.setOnClickListener {
            if(Firebase.auth.currentUser != null) {
                val action = HomeFragmentDirections.actionHomeFragmentToWriteArticleFragment()
                findNavController().navigate(action)
            } else {
                Snackbar.make(view, "로그인 후 사용해주세요",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpRecyclerView() {
        articleAdapter = HomeArticleAdapter {
            /*findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToArticleFragment(
                articleId = it.articleId.orEmpty()
            ))
            아마 오류가 난다면 여기서 날거같음
            action저거 보고 한번 다시 설정해보기
            */
        }

        binding.homeRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = articleAdapter
        }
    }
}