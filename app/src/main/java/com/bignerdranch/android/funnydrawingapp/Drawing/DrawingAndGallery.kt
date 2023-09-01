package com.bignerdranch.android.funnydrawingapp.Drawing

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bignerdranch.android.funnydrawingapp.PhotoGallery.PhotoGalleryActivity
import com.bignerdranch.android.funnydrawingapp.R
import com.bignerdranch.android.funnydrawingapp.databinding.DrawingAndGalleryFragmentBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@SuppressLint("StaticFieldLeak")
private lateinit var brushDialog: BrushDialog
private var result = ""

class DrawingAndGallery : Fragment() {

    companion object {
        fun newInstance() = DrawingAndGallery()
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
        const val TAG = "DrawingAndGallery"
    }

    // A variable for current color is picked from color pallet.
    private var imageButtonCurrentPaint: ImageButton? = null
    var customProgressDialog: Dialog? = null
    private var binding: DrawingAndGalleryFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DrawingAndGalleryFragmentBinding.inflate(inflater, container, false)
        updateBackgroundImage()
        binding!!.drawingView.setSizeForBrush(20.toFloat())

        /**
         * This is to select the default Image button which is
         * active and color is already defined in the drawing view class.
         * array list start position is 0 so the black color is at position 1.
         */
        imageButtonCurrentPaint = binding!!.colorLayout[1] as ImageButton
        imageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.pallet_selected)
        )

        binding!!.brushButton.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        brushDialog = BrushDialog(requireActivity())

        // To google image search
        binding!!.searchButton.setOnClickListener { view: View ->
            startActivity(Intent(context, PhotoGalleryActivity::class.java))
        }

        binding!!.galleryButton.setOnClickListener {
            checkSelfPermission()
        }
        binding!!.undoButton.setOnClickListener {
            binding!!.drawingView.onClickUndo()
        }
        binding!!.saveButton.setOnClickListener {
            if (isPermissionAllowed()) {
                showProgressDialog()
                lifecycleScope.launch {
                    saveBitmapFile(getBitmapFromView(binding!!.flDrawingViewContainer))
                }
            } else {
                checkSelfPermission()
            }
        }

        binding!!.switchBackgroundButton.setOnClickListener {
            showAndHideBackground()
        }

        binding!!.resetButton.setOnClickListener {
            Toast.makeText(
                context,
                R.string.restart_app,
                Toast.LENGTH_LONG
            ).show()
            binding?.drawingView?.reset()
        }
        return binding!!.root
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
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun isPermissionAllowed() = ContextCompat.checkSelfPermission(
        requireActivity(),
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
                    requireActivity(),
                    "Permission granted for storage",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Need permission to add a background. You can also allow it from the setting",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Create bitmap from view and returns it
     */

    private fun getBitmapFromView(view: View): Bitmap {
        val returnBitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        //bind canvas on the view
        val canvas = Canvas(returnBitmap)
        val backgroundDrawable = view.background
        if (backgroundDrawable != null) {
            backgroundDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        // finalize
        view.draw(canvas)

        return returnBitmap
    }

    private suspend fun saveBitmapFile(bitmap: Bitmap?): String {
        // where file will be saved
        withContext(Dispatchers.IO) {
            if (bitmap != null) {
                try {
                    // Store on device on specific location
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    // Sort files return and give them unique name
                    val f = File(
                        Environment.getExternalStorageDirectory().absolutePath + "/DCIM/FunnyImage" + System.currentTimeMillis() / 1000 + ".png"
                    )
                    // Create a file output stream, write and close
                    val fos = FileOutputStream(f)
                    fos.write(bytes.toByteArray())
                    fos.close()
                    //Save file location
                    result = f.absolutePath

                    activity?.runOnUiThread {
                        cancelProgressDialog()
                        if (!result.isEmpty()) {
                            Toast.makeText(
                                requireActivity(),
                                "File saved successfully: $result",
                                Toast.LENGTH_SHORT
                            ).show()
                            shareImage(result)
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "Something went wrong while saving",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    // override for the GALLERY code result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                // If works
                try {
                    if (data!!.data != null) {
                        binding?.ivBackground?.visibility = View.VISIBLE
                        binding?.ivBackground?.setImageURI(data.data)
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Error in parsing the image or its corrupted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    // If not work
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showBrushSizeChooserDialog() {
        brushDialog.show()

        brushDialog.apply {
            smallButton.setOnClickListener {
                binding?.drawingView?.setSizeForBrush(10.toFloat())
                dismiss()
            }
            mediumButton.setOnClickListener {
                binding?.drawingView?.setSizeForBrush(15.toFloat())
                dismiss()
            }
            largeButton.setOnClickListener {
                binding?.drawingView?.setSizeForBrush(20.toFloat())
                dismiss()
            }
        }
    }

    fun paintClicked(view: View) {
        if (view !== imageButtonCurrentPaint) {
            val imageButton = view as ImageButton

            val colorTag = imageButton.tag.toString()
            // Here the tag is used for swapping the current color with previous color.
            binding?.drawingView?.setColor(colorTag)
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(requireActivity(), R.drawable.pallet_selected)
            )
            imageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(requireActivity(), R.drawable.pallet_normal)
            )
            imageButtonCurrentPaint = view
        }
    }

    private fun showProgressDialog() {
        customProgressDialog = Dialog(requireActivity())
        /**
         * Set the screen content from a layout resource
         * The resource will be inflated, adding all top-level views to the screen
         */
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
        //Start the dialog and display it on screen
        customProgressDialog?.show()
    }

    private fun cancelProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

    // Share image to others
    private fun shareImage(result: String) {
        MediaScannerConnection.scanFile(requireActivity(), arrayOf(result), null) { _, uri ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/png"
            val chooserIntent = Intent.createChooser(shareIntent, "Share")
            startActivity(chooserIntent)
        }
    }

    fun updateBackgroundImage() {
        val sharedPreferences =
            context?.getSharedPreferences("image link", Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val link = sharedPreferences.getString("image link", "")
            // set background image
            if (link != null) {
                setImage(link)
            }
        }
    }

    private fun setImage(link: String) {
        binding?.ivBackground?.visibility = View.VISIBLE
        binding?.let {
            Glide.with(requireContext())
                .load(link)
                .into(it.ivBackground)
        }
    }

    private fun showAndHideBackground() {
        if (binding?.ivBackground?.visibility == View.VISIBLE) {
            binding?.ivBackground?.visibility = View.INVISIBLE
        } else {
            binding?.ivBackground?.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}