package com.bignerdranch.android.funnydrawingapp.api

import com.bignerdranch.android.funnydrawingapp.PhotoGallery.GoogleResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

//Set up retrofit interface to define Api calls
interface GoogleApi {
    // define fetch photo request
    @GET("https://www.googleapis.com/customsearch/v1?")
    fun fetchPhotos(): Call<GoogleResponse>
    @GET
    fun fetchUrlBytes(@Url url:String):Call<ResponseBody>
    @GET("services/rest?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query: String): Call<GoogleResponse>
}