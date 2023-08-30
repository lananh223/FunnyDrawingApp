package com.bignerdranch.android.funnydrawingapp.PhotoGallery.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class GalleryItem(
    var title: String = "",
    var id: String="",
    @SerializedName("url_s") var url: String ="",
    var owner: String =""
){
    val photoPageUri: Uri
        get() {
            return Uri.parse("https://pixabay.com/")
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build()
        }
}
