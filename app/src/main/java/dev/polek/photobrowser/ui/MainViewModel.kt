package dev.polek.photobrowser.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.polek.photobrowser.repository.FlickrRepository
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val repository: FlickrRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState?>()
    val uiState: LiveData<UiState?>
        get() = _uiState

    init {
        loadData()
    }

    fun refresh() {
        loadData()
    }

    private fun loadData() {
        val currentState = _uiState.value
        val currentPhotos = when (currentState) {
            is UiState.Loading -> currentState.photos
            is UiState.Loaded -> currentState.photos
            else -> null
        }

        _uiState.value = UiState.Loading(currentPhotos)

        viewModelScope.launch {
            try {
                val photos = repository.recentPhotos()
                _uiState.postValue(UiState.Loaded(photos))
            } catch (e: Throwable) {
                _uiState.postValue(UiState.Error(e.message.orEmpty()))
            }
        }
    }
}
