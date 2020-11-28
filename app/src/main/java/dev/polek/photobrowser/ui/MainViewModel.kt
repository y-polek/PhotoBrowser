package dev.polek.photobrowser.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.polek.photobrowser.model.Photo
import dev.polek.photobrowser.repository.FlickrRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(
    private val repository: FlickrRepository
) : ViewModel() {

    private val photos = MutableLiveData<List<Photo>>()

    init {
        viewModelScope.launch {
            val page = repository.recentPhotos()
            photos.postValue(page.photos)
        }
    }

    fun photos(): LiveData<List<Photo>> = photos
}
