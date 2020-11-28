package dev.polek.photobrowser.repository

import dev.polek.photobrowser.api.FlickrPhoto
import dev.polek.photobrowser.api.FlickrService
import dev.polek.photobrowser.model.Photo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepository @Inject constructor(
    private val service: FlickrService
) {
    suspend fun recentPhotos(): List<Photo> {
        return service.recentPhotos(1)
            .filter(FlickrPhoto::isValid)
            .map { it.toPhoto() }
    }

    private companion object {

        private fun FlickrPhoto.toPhoto(): Photo {
            return Photo(
                id = this.id.orEmpty(),
                url = this.url(),
                title = this.title.orEmpty()
            )
        }

        private fun FlickrPhoto.url(): String {
            val sizeSuffix = if (originalSecret != null) {
                // Longest edge = 1600 px
                "${originalSecret}_h"
            } else {
                // Longest edge = 1024 px
                "${secret}_b"
            }
            return "https://live.staticflickr.com/$server/${id}_$sizeSuffix.jpg"
        }
    }
}
