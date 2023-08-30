package com.bignerdranch.android.funnydrawingapp.PhotoGallery

import com.bignerdranch.android.funnydrawingapp.PhotoGallery.model.Photo
import com.google.gson.annotations.SerializedName

// api package
//hit is the key for data from the json file
class DataResponse {
    @SerializedName("hits")
     var photos: List<Photo>? = null
}
