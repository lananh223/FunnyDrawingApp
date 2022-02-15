package com.bignerdranch.android.funnydrawingapp.PhotoGallery

import com.google.gson.annotations.SerializedName
// map photos object to Json data
class PhotoResponse {
        @SerializedName("photo")
        lateinit var galleryItems: List<GalleryItem>
}