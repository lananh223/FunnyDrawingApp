package com.bignerdranch.android.funnydrawingapp.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bignerdranch.android.funnydrawingapp.PhotoGallery.GalleryItem
import com.bignerdranch.android.funnydrawingapp.PhotoGallery.GoogleResponse
import com.bignerdranch.android.funnydrawingapp.PhotoGallery.PhotoResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
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
        //using retrofit object to create an instance of Api
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.google.com/imghp?hl=en")
            // Add converter from the string to the calls (input from user)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        googleApi = retrofit.create(GoogleApi::class.java)
    }

    //Executing web request
    fun fetchPhotosRequest(): Call<GoogleResponse> {
        return googleApi.fetchPhotos()
    }
    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(fetchPhotosRequest())
    }

    fun searchPhotosRequest(query:String):Call<GoogleResponse> {
        return googleApi.searchPhotos(query)
    }
    fun searchPhotos(query: String): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(searchPhotosRequest(query))
    }
    private fun fetchPhotoMetadata(googleRequest: Call<GoogleResponse>)
            : LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()

        googleRequest.enqueue(object : Callback<GoogleResponse> {
            override fun onFailure(call: Call<GoogleResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }

            override fun onResponse(call: Call<GoogleResponse>,
                                    response: Response<GoogleResponse>) {
                Log.d(TAG, "Response received")
                val googleResponse: GoogleResponse? = response.body()
                val photoResponse: PhotoResponse? = googleResponse?.photos
                var galleryItems:List<GalleryItem> = photoResponse?.galleryItems
                    ?: mutableListOf()
                    galleryItems = galleryItems.filterNot {
                        it.url.isBlank()
                    }
                responseLiveData.value = galleryItems
            }
        })
        return responseLiveData
    }
    //Add image download
    @WorkerThread
    fun fetchPhoto(url:String): Bitmap? {
        val response: Response<ResponseBody> = googleApi.fetchUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "Decode bitmap=$bitmap from Response = $response")
        return bitmap
    }
}