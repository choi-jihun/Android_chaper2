package com.example.chapter3_10

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.chapter3_10.data.ArticleModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class HomeFragment: Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore

        db.collection("articles").document("rzjK6i9N8MuRlY9Few9W").get().addOnSuccessListener {result->
            val article = result.toObject<ArticleModel>()
            Log.e("homeFragment",article.toString())
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}