package com.bignerdranch.android.funnydrawingapp.PhotoGallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bignerdranch.android.funnydrawingapp.PhotoGallery.model.Photo
import com.bignerdranch.android.funnydrawingapp.api.PixabayFetch
import com.bignerdranch.android.photogallery.QueryPreferences

//Keep data when rotate device
class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
    var photosLiveData: LiveData<List<Photo>> = PixabayFetch().fetchPhotos()
    private val pixabayFetchr = PixabayFetch()
    private val mutableSearchTerm = MutableLiveData<String>()

    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)

        photosLiveData = Transformations.switchMap(mutableSearchTerm) { searchQuery ->
            pixabayFetchr.fetchPhotos(searchQuery)
        }
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoreQuery(app, query)
        mutableSearchTerm.value = query
    }
}