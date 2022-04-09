package com.bignerdranch.android.funnydrawingapp.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bignerdranch.android.funnydrawingapp.PhotoGallery.GoogleResponse
import com.bignerdranch.android.funnydrawingapp.PhotoGallery.model.Photo
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "GoogleFetchr"
// it's basic repository, which encapsulates the logic for
// accessing data from single/set of sources
class GoogleFetchr {
    private val googleApi: GoogleApi

    init {
        //Add interceptor to Retrofit configuration
        val client = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()
        val gson = GsonBuilder().create()
        //using retrofit object to create an instance of Api
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.google.com/")
            // Add converter from the string to the calls (input from user)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        googleApi = retrofit.create(GoogleApi::class.java)
    }

    fun fetchPhotos(searchQuery: String = ""): LiveData<List<Photo>> {
        return fetchPhotosLiveData(searchQuery)
    }

    private fun fetchPhotosLiveData(searchQuery: String = ""): LiveData<List<Photo>> {
        val responseLiveData: MutableLiveData<List<Photo>> = MutableLiveData()

        //request to fetch (get) photos
        googleApi.fetchPhotos(searchQuery)
            .enqueue(object : Callback<GoogleResponse> {
                override fun onFailure(call: Call<GoogleResponse>, t: Throwable) {
                    Log.e(TAG, "Failed to fetch photos", t)
                }

                override fun onResponse(
                    call: Call<GoogleResponse>,
                    response: Response<GoogleResponse>
                ) {
                    Log.d(TAG, "Response received: $response")
                    val googleResponse: GoogleResponse? = response.body()
                    val photos: List<Photo> = googleResponse?.photos ?: emptyList()
                    responseLiveData.value = photos
                }
            })
        return responseLiveData
    }
}