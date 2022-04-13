package com.bignerdranch.android.funnydrawingapp.api

import com.bignerdranch.android.funnydrawingapp.PhotoGallery.GoogleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

private val GOOGLE_API_KEY = "AIzaSyBkcIvSJQFSDscpc9Q79AE_TdOLbe_Mi68"

//Set up retrofit interface to define Api calls
interface GoogleApi {
    // define fetch photo request

    @GET("https://www.googleapis.com/customsearch/v1?")
    fun fetchPhotos(
        @Query("q") searchQuery: String,
        @Query("key") apiKey: String = GOOGLE_API_KEY,
        @Query("cx") cx: String = "625d03639e87f99d9",
        @Query("searchType") searchType: String = "image"
    ): Call<GoogleResponse>
}