package com.myapps.thenewsapp.repository

import com.myapps.thenewsapp.api.RetrofitInstance
import com.myapps.thenewsapp.db.ArticleDatabase
import com.myapps.thenewsapp.models.Article

class NewsRepository(val db: ArticleDatabase) {

    suspend fun getHeadLines(countryCode:String,pageNumber:Int) =
        RetrofitInstance.api.getHeadlines(countryCode,pageNumber)

    suspend fun searchNews(searchQuery:String,pageNumber:Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: Article) =
        db.getArticleDao().upsert(article)

    fun getFavouriteNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}