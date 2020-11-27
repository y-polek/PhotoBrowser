package dev.polek.photobrowser.api

import androidx.annotation.IntRange
import com.serjltt.moshi.adapters.Wrapped
import dev.polek.photobrowser.model.PhotoPage
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrService {

    @GET("rest/?method=flickr.photos.getrecent&format=json&nojsoncallback=1")
    @Wrapped(path = ["photos"])
    suspend fun recentPhotos(@Query("page") @IntRange(from = 1) page: Int): PhotoPage
}
