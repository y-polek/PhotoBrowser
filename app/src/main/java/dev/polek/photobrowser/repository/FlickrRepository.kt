package dev.polek.photobrowser.repository

import dev.polek.photobrowser.api.FlickrService
import dev.polek.photobrowser.model.PhotoPage
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepository @Inject constructor(
    private val service: FlickrService
) {
    suspend fun recentPhotos(): PhotoPage = service.recentPhotos(1)
}
