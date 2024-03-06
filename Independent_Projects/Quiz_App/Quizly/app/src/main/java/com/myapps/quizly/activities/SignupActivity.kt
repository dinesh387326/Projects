package com.myapps.quizly.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.myapps.quizly.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener{
            signUpUser()
        }
        binding.txtLogIn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUpUser(){
        val email = binding.etEmailAddressS.text.toString()
        val password = binding.etPasswordS.text.toString()
        val confirmPassword = binding.etConfirmPasswordS.text.toString()

        if(email.isBlank() || password.isBlank() || confirmPassword.isBlank()){
            Toast.makeText(this,"Email and password can't be blank",Toast.LENGTH_SHORT).show()
            return
        }

        if(password!=confirmPassword){
            Toast.makeText(this,"Email and password can't be blank",Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){
            if(it.isSuccessful){
                Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"Error creating user",Toast.LENGTH_SHORT).show()
            }
        }
    }
}