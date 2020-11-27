package dev.polek.photobrowser.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoPage(
    @Json(name = "page") val page: Int?,
    @Json(name = "photo") val photos: List<Photo>?
)
