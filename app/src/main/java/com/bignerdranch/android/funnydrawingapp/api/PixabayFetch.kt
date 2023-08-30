package com.bignerdranch.android.funnydrawingapp.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bignerdranch.android.funnydrawingapp.PhotoGallery.DataResponse
import com.bignerdranch.android.funnydrawingapp.PhotoGallery.model.Photo
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "PixabayFetchr"
// it's basic repository, which encapsulates the logic for
// accessing data from single/set of sources
class PixabayFetch {
    private val pixabayAPI: PixaBayAPI

    init {
        //Add interceptor to Retrofit configuration
        val client = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()
        val gson = GsonBuilder().create()
        //using retrofit object to create an instance of Api
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            // Add converter from the string to the calls (input from user)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        pixabayAPI = retrofit.create(PixaBayAPI::class.java)
    }

    fun fetchPhotos(searchQuery: String = ""): LiveData<List<Photo>> {
        return fetchPhotosLiveData(searchQuery)
    }

    private fun fetchPhotosLiveData(searchQuery: String = ""): LiveData<List<Photo>> {
        val responseLiveData: MutableLiveData<List<Photo>> = MutableLiveData()

        //request to fetch (get) photos
        pixabayAPI.fetchPhotos(searchQuery)
            .enqueue(object : Callback<DataResponse> {
                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    Log.e(TAG, "Failed to fetch photos", t)
                }

                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    Log.d(TAG, "Response received: $response")
                    val dataResponse: DataResponse? = response.body()
                    val photos: List<Photo> = dataResponse?.photos ?: emptyList()
                    responseLiveData.value = photos
                }
            })
        return responseLiveData
    }
}