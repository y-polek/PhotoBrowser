package dev.polek.photobrowser.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Photo(
    @Json(name = "id") val id: String?,
    @Json(name = "title") val title: String?
)
