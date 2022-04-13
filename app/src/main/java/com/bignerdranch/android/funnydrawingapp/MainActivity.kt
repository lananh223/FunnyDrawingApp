package com.bignerdranch.android.funnydrawingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.funnydrawingapp.Drawing.DrawingAndGallery

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, DrawingAndGallery.newInstance(), DrawingAndGallery.TAG)
                .commit()
        }
    }

    fun paintClicked(view: View) {
        // get painClicked function from the Fragment
        val fragment = supportFragmentManager.findFragmentByTag(DrawingAndGallery.TAG) as DrawingAndGallery
        fragment.paintClicked(view)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, DrawingAndGallery::class.java)
        }
    }
}