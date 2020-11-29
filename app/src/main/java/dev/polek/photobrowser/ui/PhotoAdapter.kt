package dev.polek.photobrowser.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import dev.polek.photobrowser.databinding.PhotoLayoutBinding
import dev.polek.photobrowser.model.Photo
import javax.inject.Inject

class PhotoAdapter @Inject constructor(
    private val glideRequestManager: RequestManager
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    var photos: List<Photo> = emptyList()
        set(value) {
            val diff = DiffUtil.calculateDiff(DiffCallback(field, value))
            field = value
            diff.dispatchUpdatesTo(this)
        }

    private val imageRequestListener = object : RequestListener<Drawable> {
        override fun onResourceReady(
            resource: Drawable,
            model: Any?,
            target: Target<Drawable>,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ): Boolean {
            (target as? ViewHolderTarget)?.holder?.binding?.errorView?.text = e?.message.orEmpty()
            return false
        }

    }

    override fun getItemCount() = photos.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PhotoLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onRetryClicked = ::notifyItemChanged)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = photos[position]

        holder.showProgressView()

        glideRequestManager
            .load(photo.url)
            .centerInside()
            .listener(imageRequestListener)
            .into(holder.imageTarget)

        holder.binding.title.apply {
            text = photo.title
            isVisible = photo.title.isNotBlank()
        }
    }

    class ViewHolder(
        val binding: PhotoLayoutBinding,
        val onRetryClicked: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        val imageTarget = ViewHolderTarget(this)
        private val onRetryClickedListener = View.OnClickListener {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return@OnClickListener

            onRetryClicked(position)
        }

        fun showProgressView() {
            binding.progressBar.isVisible = true
            binding.errorView.isVisible = false
            binding.root.setOnClickListener(null)
        }

        fun showImageView() {
            binding.progressBar.isVisible = false
            binding.errorView.isVisible = false
            binding.root.setOnClickListener(null)
        }

        fun showErrorView() {
            binding.progressBar.isVisible = false
            binding.errorView.isVisible = true
            binding.root.setOnClickListener(onRetryClickedListener)
        }
    }

    class ViewHolderTarget(
        val holder: ViewHolder
    ) : CustomViewTarget<ImageView, Drawable>(holder.binding.imageView) {

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            holder.showImageView()
            view.setImageDrawable(resource)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            holder.showErrorView()
        }

        override fun onResourceCleared(placeholder: Drawable?) {
            holder.showImageView()
            view.setImageDrawable(placeholder)
        }
    }

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
