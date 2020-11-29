package dev.polek.photobrowser.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.polek.photobrowser.databinding.PhotoLayoutBinding
import dev.polek.photobrowser.model.Photo

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    var photos: List<Photo> = emptyList()
        set(value) {
            val diff = DiffUtil.calculateDiff(DiffCallback(field, value))
            field = value
            diff.dispatchUpdatesTo(this)
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

        holder.binding.title.apply {
            text = photo.title
            isVisible = photo.title.isNotBlank()
        }
    }

    class ViewHolder(val binding: PhotoLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    private class DiffCallback(
        val oldPhotos: List<Photo>,
        val newPhotos: List<Photo>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldPhotos.size

        override fun getNewListSize() = newPhotos.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPhotos[oldItemPosition].id == newPhotos[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPhotos[oldItemPosition] == newPhotos[newItemPosition]
        }
    }
}
