package com.myapps.quizly.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.myapps.quizly.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnLogin.setOnClickListener{
            loginUser()
        }

        binding.btnSignUp.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(){
        val email = binding.etEmailAddress.text.toString()
        val password = binding.etPassword.text.toString()

        if(email.isBlank() || password.isBlank()){
            Toast.makeText(this,"Email/Password cannot be empty",Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
            if(it.isSuccessful){
                Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }
}