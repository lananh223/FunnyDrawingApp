package com.bignerdranch.android.funnydrawingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.bignerdranch.android.funnydrawingapp.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

@SuppressLint("StaticFieldLeak")
private lateinit var brushDialog: BrushDialog

class MainActivity : AppCompatActivity() {

    private var imageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.drawingView.setSizeForBrush(20.toFloat())

        imageButtonCurrentPaint = binding.colorLayout[1] as ImageButton
        imageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_selected)
        )

        binding.brushButton.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        brushDialog = BrushDialog(this)

        binding.galleryButton.setOnClickListener {
            checkSelfPermission()
        }
    }

    private fun checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // run code to get image from gallery
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, GALLERY)
        } else {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Permission granted for storage",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Need permission to add a background. You can also allow it from the setting",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }
   // override for the GALLERY code result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                // If works
                try{
                    if(data!!.data != null){
                        binding.ivBackground.visibility = View.VISIBLE
                        binding.ivBackground.setImageURI(data.data)
                    } else {
                        Toast.makeText(
                            this,
                            "Error in parsing the image or its corrupted",
                            Toast.LENGTH_SHORT
                        )
                    }
                } catch(e: Exception){
                    // If not work
                    e.printStackTrace()
                }
            }
        }
    }

    fun showBrushSizeChooserDialog() {
        brushDialog.show()

        brushDialog.apply {
            smallButton.setOnClickListener {
                binding.drawingView.setSizeForBrush(10.toFloat())
                dismiss()
            }
            mediumButton.setOnClickListener {
                binding.drawingView.setSizeForBrush(20.toFloat())
                dismiss()
            }
            largeButton.setOnClickListener {
                binding.drawingView.setSizeForBrush(30.toFloat())
                dismiss()
            }
        }
    }

    fun paintClicked(view: View) {
        if (view !== imageButtonCurrentPaint) {
            val imageButton = view as ImageButton

            val colorTag = imageButton.tag.toString()
            binding.drawingView.setColor(colorTag)
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_selected)
            )
            imageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            imageButtonCurrentPaint = view
        }
    }
}