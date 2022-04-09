package com.bignerdranch.android.funnydrawingapp.PhotoGallery.model

import com.google.gson.annotations.SerializedName
// map photos object to Json data
class Photo {
        @SerializedName("link")
        lateinit var photoLink: String
}