package com.myapps.quizly.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.myapps.quizly.R
import com.myapps.quizly.databinding.ActivityProfileBinding
import com.myapps.quizly.databinding.ActivityResultBinding

class ProfileActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.textEmail.text = firebaseAuth.currentUser?.email

        binding.buttonLogOut.setOnClickListener{
            firebaseAuth.signOut()
            val intent = Intent(this,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finishAffinity()
        }
    }
}