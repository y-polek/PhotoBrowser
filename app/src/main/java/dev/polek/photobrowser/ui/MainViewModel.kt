package dev.polek.photobrowser.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.polek.photobrowser.repository.FlickrRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(
    private val repository: FlickrRepository
) : ViewModel() {

    fun loadData() {
        viewModelScope.launch {
            val photos = repository.recentPhotos()
            Timber.d("Photos: $photos")
        }
    }
}
