package dev.polek.photobrowser.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dev.polek.photobrowser.model.Photo
import dev.polek.photobrowser.repository.FlickrRepository
import dev.polek.photobrowser.utils.MainCoroutineScopeRule
import dev.polek.photobrowser.utils.getValueForTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.assertj.core.api.Assertions.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope =  MainCoroutineScopeRule()

    @Test
    fun `test successful image list loading`() {
        val photos = listOf(
            Photo("1", "1.jpg", "photo 1"),
            Photo("2", "2.jpg", "photo 3"),
            Photo("3", "3.jpg", "photo 3"),
        )
        val delayMillis = 1000L
        val repository = object : FlickrRepository {
            override suspend fun recentPhotos(): List<Photo> {
                delay(delayMillis)
                return photos
            }
        }
        val viewModel = MainViewModel(repository)

        var state = viewModel.uiState.getValueForTest()
        assertThat(state).isExactlyInstanceOf(UiState.Loading::class.java)
        assertThat((state as UiState.Loading).photos).isNull()

        coroutineScope.advanceTimeBy(delayMillis)

        state = viewModel.uiState.getValueForTest()
        assertThat(state).isExactlyInstanceOf(UiState.Loaded::class.java)
        assertThat((state as UiState.Loaded).photos).containsExactly(*photos.toTypedArray())
    }

    @Test
    fun `test failed image list loading`() {
        val errorMessage = "No network connection"
        val delayMillis = 1000L
        val repository = object : FlickrRepository {
            override suspend fun recentPhotos(): List<Photo> {
                delay(delayMillis)
                throw RuntimeException(errorMessage)
            }
        }
        val viewModel = MainViewModel(repository)

        var state = viewModel.uiState.getValueForTest()
        assertThat(state).isExactlyInstanceOf(UiState.Loading::class.java)
        assertThat((state as UiState.Loading).photos).isNull()

        coroutineScope.advanceTimeBy(delayMillis)

        state = viewModel.uiState.getValueForTest()
        assertThat(state).isExactlyInstanceOf(UiState.Error::class.java)
        assertThat((state as UiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `test Loading state contains list of previous photos on refresh`() {
        val photo1 = Photo("1", "1.jpg", "photo 1")
        val photo2 = Photo("2", "2.jpg", "photo 2")
        val photo3 = Photo("3", "3.jpg", "photo 3")
        val delayMillis = 1000L
        val repository = object : FlickrRepository {
            private var callCount = 0
            override suspend fun recentPhotos(): List<Photo> {
                callCount++
                delay(delayMillis)
                return when (callCount) {
                    1 -> listOf(photo2, photo1)
                    2 -> listOf(photo3, photo2, photo1)
                    else -> throw IllegalStateException("FlickrRepository.recentPhotos() called too many times")
                }
            }
        }
        val viewModel = MainViewModel(repository)
        coroutineScope.advanceTimeBy(delayMillis)

        viewModel.refresh()

        var state = viewModel.uiState.getValueForTest()
        assertThat(state).isExactlyInstanceOf(UiState.Loading::class.java)
        assertThat((state as UiState.Loading).photos).containsExactly(photo2, photo1)

        coroutineScope.advanceTimeBy(delayMillis)

        state = viewModel.uiState.getValueForTest()
        assertThat(state).isExactlyInstanceOf(UiState.Loaded::class.java)
        assertThat((state as UiState.Loaded).photos).containsExactly(photo3, photo2, photo1)
    }

    @Test
    fun `test Loading state does not contains list of previous photos on refresh after Error`() {
        val photo1 = Photo("1", "1.jpg", "photo 1")
        val photo2 = Photo("2", "2.jpg", "photo 2")
        val photo3 = Photo("3", "3.jpg", "photo 3")
        val delayMillis = 1000L
        val repository = object : FlickrRepository {
            private var callCount = 0
            override suspend fun recentPhotos(): List<Photo> {
                callCount++
                delay(delayMillis)
                return when (callCount) {
                    1 -> listOf(photo2, photo1)
                    2 -> throw RuntimeException("Error!")
                    3 -> listOf(photo3, photo2, photo1)
                    else -> throw IllegalStateException("FlickrRepository.recentPhotos() called too many times")
                }
            }
        }
        val viewModel = MainViewModel(repository)
        assertThat(viewModel.uiState.getValueForTest()).isExactlyInstanceOf(UiState.Loading::class.java)
        coroutineScope.advanceTimeBy(delayMillis)
        assertThat(viewModel.uiState.getValueForTest()).isExactlyInstanceOf(UiState.Loaded::class.java)

        viewModel.refresh()
        assertThat(viewModel.uiState.getValueForTest()).isExactlyInstanceOf(UiState.Loading::class.java)
        coroutineScope.advanceTimeBy(delayMillis)
        assertThat(viewModel.uiState.getValueForTest()).isExactlyInstanceOf(UiState.Error::class.java)

        viewModel.refresh()
        val state = viewModel.uiState.getValueForTest()
        assertThat(state).isExactlyInstanceOf(UiState.Loading::class.java)
        assertThat((state as UiState.Loading).photos).isNull()

        coroutineScope.advanceTimeBy(delayMillis)
        assertThat(viewModel.uiState.getValueForTest()).isExactlyInstanceOf(UiState.Loaded::class.java)
    }
}
