package com.example.chapter3_6

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter3_6.Key.Companion.DB_USERS
import com.example.chapter3_6.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"이메일 혹은 패스워드가 입력되지 않았습니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {task->
                if(task.isSuccessful){
                    Toast.makeText(this,"회원가입에 성공했습니다.",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"회원가입에 실패했습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"이메일 혹은 패스워드가 입력되지 않았습니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){task->
                val currentUser = Firebase.auth.currentUser
                if(task.isSuccessful && currentUser != null){

                    val userId = currentUser.uid

                    Firebase.messaging.token.addOnCompleteListener {
                        val token = it.result
                        val user = mutableMapOf<String, Any>()
                        user["userId"] = userId
                        user["username"] = email
                        user["fcmToken"] = token

                        Firebase.database.reference.child(DB_USERS).child(userId).updateChildren(user)
                    }

                     //서버가 미국이 아니면 참조URL 넣어줘야함
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    Log.e("LoginActivity",task.exception.toString())
                    Toast.makeText(this,"로그인에 실패했습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}