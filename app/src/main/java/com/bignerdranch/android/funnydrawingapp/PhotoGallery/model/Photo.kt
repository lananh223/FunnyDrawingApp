package com.bignerdranch.android.funnydrawingapp.PhotoGallery.model

import com.google.gson.annotations.SerializedName
// map photos object to Json data
//webformatURL is the key for data from the json file
class Photo {
        @SerializedName("webformatURL")
        lateinit var photoLink: String
}