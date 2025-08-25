package com.myapps.thenewsapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myapps.thenewsapp.models.Article
import com.myapps.thenewsapp.models.ArticleResponse
import com.myapps.thenewsapp.repository.NewsRepository
import com.myapps.thenewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app:Application, val newsRepository: NewsRepository): AndroidViewModel(app) {
    val headlines: MutableLiveData<Resource<ArticleResponse>> = MutableLiveData()
    var headlinesPage = 1
    var headlinesResponse: ArticleResponse? = null

    val searchNews: MutableLiveData<Resource<ArticleResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: ArticleResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    init {
        getHeadlines("us")
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    private fun handleHeadlinesResponse(response: Response<ArticleResponse>): Resource<ArticleResponse>{
        if(response.isSuccessful){
            response.body()?.let{
                resultResponse -> headlinesPage++
                if(headlinesResponse == null){
                    headlinesResponse = resultResponse
                }else{
                    headlinesResponse?.articles?.addAll(resultResponse.articles)
                }
                return Resource.Success(headlinesResponse ?:  resultResponse)
            }
        }
        return Resource.Error(response.message(),headlinesResponse)
    }

    private fun handleSearchNewsResponse(response: Response<ArticleResponse>) : Resource<ArticleResponse>{
        if(response.isSuccessful){
            response.body()?.let{
                resultResponse ->
                if(searchNewsResponse == null || newSearchQuery != oldSearchQuery){
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                }
                else{
                    searchNewsPage++
                    searchNewsResponse?.articles?.addAll(resultResponse.articles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message(),searchNewsResponse)
    }

    fun addToFavourites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getFavouriteNews() = newsRepository.getFavouriteNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun internetConnection(context: Context) : Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run{
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    private suspend fun headlinesInternet(countryCode: String){
        headlines.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication())){
                val response = newsRepository.getHeadLines(countryCode, headlinesPage)
                headlines.postValue(handleHeadlinesResponse(response))
            }else{
                headlines.postValue(Resource.Error("No internet connection",headlinesResponse))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Unable to Connect",headlinesResponse))
                else -> headlines.postValue(Resource.Error("No signal",headlinesResponse))
            }
        }
    }

    private suspend fun searchNewsInternet(searchQuery: String){
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication())){
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No internet connection",searchNewsResponse))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Unable to Connect",searchNewsResponse))
                else -> searchNews.postValue(Resource.Error("No signal",searchNewsResponse))
            }
        }
    }
}