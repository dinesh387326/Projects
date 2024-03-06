package com.myapps.quizly.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.myapps.quizly.R
import com.myapps.quizly.activities.QuestionActivity
import com.myapps.quizly.models.Quiz
import com.myapps.quizly.utils.ColorPicker
import com.myapps.quizly.utils.IconPicker

class QuizAdapter(val context: Context, val quizzes: List<Quiz>) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView = itemView.findViewById(R.id.quizTitle)
        var iconView: ImageView = itemView.findViewById(R.id.quizIcon)
        var cardContainer: CardView = itemView.findViewById(R.id.cardContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.quiz_item,parent,false)
        return QuizViewHolder(view)
    }

    override fun getItemCount(): Int {
        return quizzes.size
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.textViewTitle.text = quizzes[position].title
        holder.cardContainer.setCardBackgroundColor(Color.parseColor(ColorPicker.getColor()))
        holder.iconView.setImageResource(IconPicker.getIcon())
        holder.itemView.setOnClickListener{
            val intent = Intent(context,QuestionActivity::class.java)
            intent.putExtra("DATE",quizzes[position].title)
            context.startActivity(intent)
        }
    }
}