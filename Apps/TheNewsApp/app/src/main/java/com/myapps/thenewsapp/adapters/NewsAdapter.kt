package com.myapps.thenewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapps.thenewsapp.R
import com.myapps.thenewsapp.models.Article

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var articleImage: ImageView
        lateinit var articleSource: TextView
        lateinit var articleTitle: TextView
        lateinit var articleDescription: TextView
        lateinit var articleDateTime: TextView
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.articleImage = holder.itemView.findViewById(R.id.articleImage)
        holder.articleSource = holder.itemView.findViewById(R.id.articleSource)
        holder.articleDescription = holder.itemView.findViewById(R.id.articleDescription)
        holder.articleDateTime = holder.itemView.findViewById(R.id.articleDateTime)
        holder.articleTitle = holder.itemView.findViewById(R.id.articleTitle)

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(holder.articleImage)
            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
        holder.apply {
            articleTitle.text = article.title
            articleSource.text = article.source?.name
            articleDescription.text = article.description
            articleDateTime.text = article.publishedAt

        }

    }

    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }



}