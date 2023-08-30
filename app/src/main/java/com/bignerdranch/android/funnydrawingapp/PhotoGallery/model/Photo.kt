package com.bignerdranch.android.funnydrawingapp.PhotoGallery.model

import com.google.gson.annotations.SerializedName
// map photos object to Json data
//pageURL is the key for data from the json file
class Photo {
        @SerializedName("pageURL")
        lateinit var photoLink: String
}