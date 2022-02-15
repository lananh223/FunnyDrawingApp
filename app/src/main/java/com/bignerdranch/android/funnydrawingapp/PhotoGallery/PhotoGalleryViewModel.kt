package com.bignerdranch.android.funnydrawingapp.PhotoGallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bignerdranch.android.funnydrawingapp.api.GoogleFetchr
import com.bignerdranch.android.photogallery.QueryPreferences

//Keep data when rotate device
class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
    var galleryItemLiveData: LiveData<List<GalleryItem>> = GoogleFetchr().searchPhotos("cats")
    private val googleFetchr = GoogleFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()

    val searchTerm: String
        get() = mutableSearchTerm.value ?:""

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)

        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            if (searchTerm.isBlank()) {
                googleFetchr.fetchPhotos()
            } else {
                googleFetchr.searchPhotos(searchTerm)
            }
        }
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoreQuery(app, query)
        mutableSearchTerm.value = query
    }
}