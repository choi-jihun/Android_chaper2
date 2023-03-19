package com.example.chapter3_10

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chapter3_10.databinding.FragmentAuthBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthFragment: Fragment(R.layout.fragment_auth) {

    private lateinit var binding : FragmentAuthBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAuthBinding.bind(view)

        setupSignUpButton()
        setupSignInOutButton()
    }

    private fun setupSignUpButton() {
        binding.signUpBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "아이디 또는 패스워드를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(binding.root, "회원가입에 성공했습니다.", Snackbar.LENGTH_SHORT).show()
                    initViewsToSignInState()
                } else {
                    Snackbar.make(binding.root, "회원가입에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSignInOutButton() {
        binding.signInOutBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (Firebase.auth.currentUser == null) {
                //로그인 중
                if(email.isEmpty() || password.isEmpty()) {
                    Snackbar.make(binding.root, "아이디 또는 패스워드를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //로그아웃으로 바꾸기
                        initViewsToSignInState()
                    } else {
                        Snackbar.make(binding.root, "로그인에 실패했습니다. 이메일 혹은 패스워드를 확인해주세요.", Snackbar.LENGTH_SHORT).show()
                    }
                }
            } else {
                //로그아웃
                Firebase.auth.signOut()
                //로그인으로 바꾸기
                initViewsToSignOutState()
            }
        }
    }

    private fun initViewsToSignOutState() {
        binding.emailEditText.text.clear()
        binding.emailEditText.isEnabled = true
        binding.passwordEditText.isVisible = true
        binding.passwordEditText.text.clear()
        binding.signInOutBtn.text = getString(R.string.signIn)
        binding.signUpBtn.isEnabled = true
    }

    private fun initViewsToSignInState() {
        binding.emailEditText.setText(Firebase.auth.currentUser?.email)
        binding.emailEditText.isEnabled = false
        binding.passwordEditText.isVisible = false
        binding.signInOutBtn.text = getString(R.string.signOut)
        binding.signUpBtn.isEnabled = false
    }

    override fun onStart() {
        super.onStart()

        if(Firebase.auth.currentUser == null) {
            initViewsToSignOutState()
        } else {
            initViewsToSignInState()
        }
    }
}