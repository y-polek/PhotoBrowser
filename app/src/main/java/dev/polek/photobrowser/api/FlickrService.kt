package dev.polek.photobrowser.api

import androidx.annotation.IntRange
import com.serjltt.moshi.adapters.Wrapped
import dev.polek.photobrowser.model.Photo
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrService {

    @GET("rest/?method=flickr.photos.getrecent" +
            "&format=json" +
            "&per_page=500" +
            "&extras=original_format" +
            "&nojsoncallback=1"
    )
    @Wrapped(path = ["photos", "photo"])
    suspend fun recentPhotos(@Query("page") @IntRange(from = 1) page: Int): List<FlickrPhoto>
}
