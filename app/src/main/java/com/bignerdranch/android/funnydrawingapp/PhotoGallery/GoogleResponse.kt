package com.bignerdranch.android.funnydrawingapp.PhotoGallery

import com.bignerdranch.android.funnydrawingapp.PhotoGallery.model.Photo
import com.google.gson.annotations.SerializedName

// api package
class GoogleResponse {
    @SerializedName("items")
    lateinit var photos: List<Photo>
}
