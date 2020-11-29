package dev.polek.photobrowser.repository

import dev.polek.photobrowser.api.FlickrPhoto
import dev.polek.photobrowser.api.FlickrService
import dev.polek.photobrowser.model.Photo
import javax.inject.Inject
import javax.inject.Singleton

interface FlickrRepository {
    suspend fun recentPhotos(): List<Photo>
}

@Singleton
class FlickrRepositoryImpl @Inject constructor(
    private val service: FlickrService
) : FlickrRepository {

    override suspend fun recentPhotos(): List<Photo> {
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
    }
}
