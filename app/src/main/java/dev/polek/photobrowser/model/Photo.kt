package dev.polek.photobrowser.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Photo(
    val id: String,
    val url: String,
    val title: String
)
