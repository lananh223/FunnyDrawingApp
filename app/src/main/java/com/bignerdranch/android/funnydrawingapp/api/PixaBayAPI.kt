package com.bignerdranch.android.funnydrawingapp.api

import com.bignerdranch.android.funnydrawingapp.PhotoGallery.DataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "39118956-7832145f42d9e23260ab08932"

//Set up retrofit interface to define Api calls
interface PixaBayAPI {
    // define fetch photo request

    @GET("api/?key=$API_KEY" )
    fun fetchPhotos(
        @Query("q") searchQuery: String?="",
        @Query("image_type") imageType: String = "photo"
    ): Call<DataResponse>
}