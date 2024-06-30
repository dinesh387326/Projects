package com.myapps.tasty_eats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myapps.tasty_eats.databinding.ActivityIntroBinding
import com.myapps.tasty_eats.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.dontHaveAccount.setOnClickListener{
            val intent = Intent(this,SignActivity::class.java)
            startActivity(intent)
        }
    }
}