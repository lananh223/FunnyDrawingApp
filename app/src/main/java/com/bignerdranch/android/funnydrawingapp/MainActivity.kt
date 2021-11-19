package com.bignerdranch.android.funnydrawingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.bignerdranch.android.funnydrawingapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private lateinit var binding: ActivityMainBinding

@SuppressLint("StaticFieldLeak")
private lateinit var brushDialog: BrushDialog
var customProgressDialog: Dialog? = null
var result =""

class MainActivity : AppCompatActivity() {
    // A variable for current color is picked from color pallet.
    private var imageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.drawingView.setSizeForBrush(20.toFloat())

        /**
         * This is to select the default Image button which is
         * active and color is already defined in the drawing view class.
         * As the default color is black so in our color pallet it is on 2 position.
         * But the array list start position is 0 so the black color is at position 1.
         */
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
        binding.undoButton.setOnClickListener {
            binding.drawingView.onClickUndo()
        }
        binding.saveButton.setOnClickListener {
            if(isPermissionAllowed()){
                showProgressDialog()
                lifecycleScope.launch {
                    saveBitmapFile(getBitmapFromView(binding.flDrawingViewContainer))
                    }
                } else {
                    checkSelfPermission()
            }
        }
        binding.shareButton.setOnClickListener{
            shareImage(result)
        }
    }

    private fun checkSelfPermission() {
        if (isPermissionAllowed()
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

    private fun isPermissionAllowed() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

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
    }/**
     * Create bitmap from view and returns it
     */

    private fun getBitmapFromView(view: View): Bitmap{
        val returnBitmap = Bitmap.createBitmap(view.width,
            view.height, Bitmap.Config.ARGB_8888)
        //bind canvas on the view
        val canvas = Canvas(returnBitmap)
        val backgroundDrawable = view.background
        if(backgroundDrawable != null){
            backgroundDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        // finalize
        view.draw(canvas)

        return returnBitmap
    }

    private suspend fun saveBitmapFile(bitmap:Bitmap?): String{
        // where file will be saved
        withContext(Dispatchers.IO){
            if(bitmap != null){
                try {
                // Store on device on specific location
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    // Sort files return and give them unique name
                    val f = File(externalCacheDir!!.absoluteFile.toString()+
                        File.separator + "FunnyDrawingApp_" + System.currentTimeMillis()/1000 + ".png")
                    // Create a file output stream, write and close
                    val fos = FileOutputStream(f)
                    fos.write(bytes.toByteArray())
                    fos.close()

                    result = f.absolutePath

                    runOnUiThread {
                        cancelProgressDialog()
                            if (!result.isEmpty()) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "File saved successfully: $result",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Something went wrong while saving",
                                    Toast.LENGTH_SHORT
                                )
                            }
                        }
                } catch(e:Exception){
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
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

    private fun showProgressDialog(){
        customProgressDialog = Dialog(this@MainActivity)
        /**
         * Set the screen content from a layout resource
         * The resource will be inflated, adding all top-level views to the screen
         */
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
        //Start the dialog and display it on screen
        customProgressDialog?.show()
    }

    private fun cancelProgressDialog(){
        if (customProgressDialog != null){
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }
    // Share image to others
    private fun shareImage(result:String){
        MediaScannerConnection.scanFile(this, arrayOf(result), null){
            path, uri ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type="image/png"
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }
    }
}