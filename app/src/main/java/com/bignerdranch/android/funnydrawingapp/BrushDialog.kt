package com.bignerdranch.android.funnydrawingapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageButton

class BrushDialog(context: Context) : Dialog(context) {

    lateinit var smallButton: ImageButton
    lateinit var mediumButton: ImageButton
    lateinit var largeButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.diaglog_brush_size)
        setTitle("Brush size: ")

        smallButton = findViewById(R.id.small_brush_button)
        mediumButton = findViewById(R.id.medium_brush_button)
        largeButton = findViewById(R.id.large_brush_button)
    }
}