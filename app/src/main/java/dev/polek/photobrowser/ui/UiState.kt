package dev.polek.photobrowser.ui

import dev.polek.photobrowser.model.Photo

sealed class UiState {
    class Loading(val photos: List<Photo>?) : UiState()
    class Loaded(val photos: List<Photo>) : UiState()
    class Error(val message: String) : UiState()
}
