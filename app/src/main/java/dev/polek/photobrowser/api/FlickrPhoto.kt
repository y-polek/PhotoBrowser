package dev.polek.photobrowser.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlickrPhoto(
    @Json(name = "id") val id: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "server") val server: String?,
    @Json(name = "owner") val owner: String?,
    @Json(name = "secret") val secret: String?,
    @Json(name = "originalsecret") val originalSecret: String?
) {
    val isValid = id != null && server != null && owner != null && secret != null
}
