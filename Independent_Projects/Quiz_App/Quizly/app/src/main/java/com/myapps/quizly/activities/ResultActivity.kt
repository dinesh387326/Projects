package com.myapps.quizly.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.google.gson.Gson
import com.myapps.quizly.R
import com.myapps.quizly.databinding.ActivityResultBinding
import com.myapps.quizly.models.Quiz

class ResultActivity : AppCompatActivity() {
    lateinit var quiz: Quiz
    lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
        binding.buttonBack.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpViews() {
        val quizData = intent.getStringExtra("QUIZ")
        quiz = Gson().fromJson<Quiz>(quizData , Quiz::class.java)
        calculateScore()
        setAnswerView()
    }

    private fun setAnswerView() {
        val builder = StringBuilder("")
        for(entry in quiz.questions.entries){
            val question = entry.value
            builder.append("<font color'#18206F'><b>Question: ${question.description}</b></font><br/><br/>")
            builder.append("<font color = '#009688'>Answer: ${question.answer} </font><br/><br/>")
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            binding.textAnswers.text = Html.fromHtml(builder.toString(), Html.FROM_HTML_MODE_COMPACT)
        }
        else{
            binding.textAnswers.text = Html.fromHtml(builder.toString())
        }
    }

    private fun calculateScore() {
        var score = 0
        for (entry in quiz.questions.entries){
            val question = entry.value
            if(question.answer == question.userAnswer){
                score+=10
            }
        }

        binding.textScore.text = "Your Score: $score"
    }
}