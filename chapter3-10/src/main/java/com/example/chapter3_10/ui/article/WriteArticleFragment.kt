package com.example.chapter3_10.ui.article

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chapter3_10.R
import com.example.chapter3_10.databinding.FragmentWriteBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class WriteArticleFragment: Fragment(R.layout.fragment_write) {

    private lateinit var binding: FragmentWriteBinding

    private var selectedUri: Uri ?= null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            selectedUri = uri
            binding.photoImageView.setImageURI(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWriteBinding.bind(view)
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        binding.photoImageView.setOnClickListener {

        }

        binding.deleteButton.setOnClickListener {

        }

        binding.submitButton.setOnClickListener {
            if(selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                val fileName = "${UUID.randomUUID()}.png"
                Firebase.storage.reference.child("articles/photo").child(fileName).putFile(photoUri).addOnCompleteListener{task->
                    if(task.isSuccessful) {
                        Firebase.storage.reference.child("articles/photo").child(fileName).downloadUrl.addOnSuccessListener {
                            Log.e("WriteArticleFragment",it.toString())
                        }.addOnFailureListener {

                        }
                    } else {
                        //error
                    }
                }
            } else {
                Snackbar.make(view,"이미지가 선택되지 않았습니다.",Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(WriteArticleFragmentDirections.actionBack())
        }
    }
}