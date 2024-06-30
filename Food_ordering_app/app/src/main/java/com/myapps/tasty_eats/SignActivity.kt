package com.myapps.tasty_eats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myapps.tasty_eats.databinding.ActivityLoginBinding
import com.myapps.tasty_eats.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {
    private val binding: ActivitySignBinding by lazy{
        ActivitySignBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.alreadyHaveAccount.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        binding.createButton.setOnClickListener{
            val intent = Intent(this,ChooseLocationActivity::class.java)
            startActivity(intent)
        }
    }
}