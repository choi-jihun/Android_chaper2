package com.example.chapter3_10.ui.bookmark

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.chapter3_10.R
import com.example.chapter3_10.databinding.FragmentBookmarkBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class BookMarkArticleFragment : Fragment(R.layout.fragment_bookmark) {

    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var bookmarkAdapter: BookmarkArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookmarkBinding.bind(view)
        bookmarkAdapter = BookmarkArticleAdapter {

        }

        binding.articleRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = bookmarkAdapter
        }
        val uid = Firebase.auth.currentUser?.uid.orEmpty()
        Firebase.firestore.collection("articles").document(uid).get().addOnSuccessListener {
            val list = it.get("articleIds") as List<*>
            if (list.isNotEmpty()) {
                Firebase.firestore.collection("articles").whereIn("articleId", list)
                    .get()
                    .addOnSuccessListener { result ->
                        bookmarkAdapter.submitList(result.map { article -> article.toObject() })
                    }.addOnFailureListener {
                        it.printStackTrace()
                    }
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}