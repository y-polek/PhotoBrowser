package dev.polek.photobrowser.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.polek.photobrowser.databinding.PhotoLayoutBinding
import dev.polek.photobrowser.model.Photo

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    var photos: List<Photo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = photos.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PhotoLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = photos[position]

        Glide.with(holder.itemView)
            .load(photo.url)
            .centerInside()
            .into(holder.binding.imageView)
    }

    class ViewHolder(val binding: PhotoLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
