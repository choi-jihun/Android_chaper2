package com.example.chapter3_10.ui.article

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chapter3_10.R
import com.example.chapter3_10.data.ArticleModel
import com.example.chapter3_10.databinding.FragmentWriteBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class WriteArticleFragment : Fragment(R.layout.fragment_write) {

    private lateinit var binding: FragmentWriteBinding

    private var selectedUri: Uri? = null
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                selectedUri = uri
                binding.photoImageView.setImageURI(uri)
                binding.plusButton.isVisible = false
                binding.deleteButton.isVisible = true
            } else {
                Toast.makeText(context, "이미지 선택을 취소하였습니다.",Toast.LENGTH_SHORT).show()
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWriteBinding.bind(view)
        startPicker()
        setUpPhotoImageView()
        setUpDeleteButton()
        setUpSubmitButton(view)
        setUpBackButton()

    }

    private fun setUpBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(WriteArticleFragmentDirections.actionBack())
        }
    }

    private fun setUpSubmitButton(view: View) {
        binding.submitButton.setOnClickListener {
            showProgress()
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                upLoadImage(photoUri,
                    successHandler = {
                        //Firestore 데이터 업로드
                        upLoadArticle(it, binding.descriptionEditText.text.toString())
                    }, errorHandler = {
                        Snackbar.make(view, "이미지 업로드에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                        hideProgress()
                    })
            } else {
                Snackbar.make(view, "이미지가 선택되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
                hideProgress()
            }
        }
    }

    private fun startPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setUpPhotoImageView() {
        binding.photoImageView.setOnClickListener {
            if (selectedUri == null) {
                startPicker()
            }
        }
    }

    private fun setUpDeleteButton() {
        binding.deleteButton.setOnClickListener {
            binding.photoImageView.setImageURI(null)
            selectedUri = null
            binding.deleteButton.isVisible = false
            binding.plusButton.isVisible = true
        }
    }

    private fun showProgress() {
        binding.progressBarLayout.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBarLayout.isVisible = false
    }

    private fun upLoadImage(
        uri: Uri,
        successHandler: (String) -> Unit,
        errorHandler: (Throwable?) -> Unit
    ) {
        val fileName = "${UUID.randomUUID()}.png"
        Firebase.storage.reference.child("articles/photo").child(fileName).putFile(uri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firebase.storage.reference.child("articles/photo")
                        .child(fileName).downloadUrl.addOnSuccessListener {
                            successHandler(it.toString())
                        }.addOnFailureListener {
                            errorHandler(it)
                        }
                } else {
                    //error
                    errorHandler(task.exception)
                }
            }
    }

    private fun upLoadArticle(photoUrl: String, description: String) {
        val articleId = UUID.randomUUID().toString()
        val articleModel = ArticleModel(
            articleId = articleId,
            createAt = System.currentTimeMillis(),
            description = description,
            imageUrl = photoUrl
        )
        Firebase.firestore.collection("articles").document(articleId).set(articleModel)
            .addOnSuccessListener {
                findNavController().navigate(WriteArticleFragmentDirections.actionWriteArticleFragmentToHomeFragment())
                view?.let {view->
                    Snackbar.make(view,"업로드 되었습니다.",Snackbar.LENGTH_SHORT).show()
                }
                hideProgress()
            }.addOnFailureListener {
                it.printStackTrace()
                view?.let { view ->
                    Snackbar.make(view, "글 작성에 실패했습니다", Snackbar.LENGTH_SHORT).show()
                }
                hideProgress()
            }

    }
}