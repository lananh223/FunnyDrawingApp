package com.bignerdranch.android.funnydrawingapp.PhotoGallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.funnydrawingapp.R

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)

        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.galleryFragmentContainer, PhotoGalleryFragment.newInstance())
                .commit()
        }
    }
}