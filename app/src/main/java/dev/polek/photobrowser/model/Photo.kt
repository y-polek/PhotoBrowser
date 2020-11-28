package dev.polek.photobrowser.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Photo(
    @Json(name = "id") val id: String?,
    @Json(name = "server") val server: String?,
    @Json(name = "owner") val owner: String?,
    @Json(name = "secret") val secret: String?,
    @Json(name = "originalsecret") val originalSecret: String?
) {
    val isValid = id != null && server != null && owner != null && secret != null
    val url: String = if (originalSecret != null) {
        "https://live.staticflickr.com/$server/${id}_${originalSecret}_h.jpg"
    } else {
        "https://live.staticflickr.com/$server/${id}_${secret}_b.jpg"
    }
}
