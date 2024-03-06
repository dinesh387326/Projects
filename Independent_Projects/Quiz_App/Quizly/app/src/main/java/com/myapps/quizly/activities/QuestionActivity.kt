package com.myapps.quizly.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.myapps.quizly.R
import com.myapps.quizly.adapters.OptionAdapter
import com.myapps.quizly.databinding.ActivityQuestionBinding
import com.myapps.quizly.models.Question
import com.myapps.quizly.models.Quiz

class QuestionActivity : AppCompatActivity() {
    lateinit var binding: ActivityQuestionBinding
    lateinit var firestore: FirebaseFirestore
    var questions: MutableMap<String,Question>? = null
    var quiz: MutableList<Quiz>? = null
    var index = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#18206F")))
        setUpFirestore()
        setUpEventListener()
    }

    private fun setUpEventListener() {
        binding.buttonNext.setOnClickListener{
            index++
            bindViews()
        }
        binding.buttonPrevious.setOnClickListener{
            index--
            bindViews()
        }
        binding.buttonSubmit.setOnClickListener{
            val intent = Intent(this,ResultActivity::class.java)
            val json = Gson().toJson(quiz!![0])
            intent.putExtra("QUIZ",json)
            startActivity(intent)
            finish()
        }
    }

    private fun setUpFirestore() {
        firestore = FirebaseFirestore.getInstance()
        val date = intent.getStringExtra("DATE")
        Log.d("CHECK", date!!)
        if(date!= null){
            firestore.collection("quizzes").whereEqualTo("title",date).get().addOnSuccessListener {
                if(it!= null && !it.isEmpty){
                    quiz = it.toObjects(Quiz::class.java)
                    questions = quiz!![0].questions
                    bindViews()
                }
                else{
                    Toast.makeText(this,"Error fetching data",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindViews() {
        binding.buttonNext.visibility = View.GONE
        binding.buttonPrevious.visibility = View.GONE
        binding.buttonSubmit.visibility = View.GONE

        if(index == 1){
            binding.buttonNext.visibility = View.VISIBLE
        }
        else if(index == questions!!.size){
            binding.buttonPrevious.visibility = View.VISIBLE
            binding.buttonSubmit.visibility = View.VISIBLE
        }
        else{
            binding.buttonPrevious.visibility = View.VISIBLE
            binding.buttonNext.visibility = View.VISIBLE
        }

        val question = questions!!["question$index"]
        question?.let {
            binding.description.text = it.description
            val optionAdapter = OptionAdapter(this,it)
            binding.optionList.layoutManager = LinearLayoutManager(this)
            binding.optionList.adapter = optionAdapter
            binding.optionList.setHasFixedSize(true)
        }

    }
}