package com.bignerdranch.android.funnydrawingapp.PhotoGallery

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.funnydrawingapp.Drawing.DrawingAndGallery
import com.bignerdranch.android.funnydrawingapp.PhotoGallery.model.Photo
import com.bignerdranch.android.funnydrawingapp.R
import com.bumptech.glide.Glide

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        //Getting ViewModel instance from provider
        photoGalleryViewModel = ViewModelProvider(this)[PhotoGalleryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        // set recycler's layout manager to new instance of GridLayout Manager
        photoRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.photosLiveData.observe(
            viewLifecycleOwner,
            Observer { photos ->
                photoRecyclerView.adapter = PhotoAdapter(photos)
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)
        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.apply {

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextSubmit: $queryText")
                    photoGalleryViewModel.fetchPhotos(queryText)
                    return true
                }

                override fun onQueryTextChange(queryText: String?): Boolean {
                    Log.d(TAG, "QueryTextChange: $queryText")
                    return false
                }
            })

            setOnClickListener {
                searchView.setQuery(photoGalleryViewModel.searchTerm, false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                photoGalleryViewModel.fetchPhotos("")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    //Add Adapter implementation
    private inner class PhotoAdapter(private val photoList: List<Photo>) :
        RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

        //Adding ViewHolder
        inner class ViewHolder(itemImageView: ImageView) : RecyclerView.ViewHolder(itemImageView) {
            val imageView: ImageView = itemImageView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(
                R.layout.list_item_gallery,
                parent,
                false
            ) as AppCompatImageView
            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(holder.itemView)
                .load(photoList[position].photoLink)
                .fitCenter()
                .into(holder.imageView)

            val link = photoList[position].photoLink
            // set on click for each item
            holder.itemView.setOnClickListener {
                saveImageLink(link)

                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.fl_drawing_view_container, DrawingAndGallery.newInstance())
                transaction.commit()
            }
        }

        override fun getItemCount(): Int = photoList.size
    }

    private fun saveImageLink(link: String) {
        val sharedPreferences =
            requireContext().getSharedPreferences("image link", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("image link", link)
            apply()
        }
    }

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}

