package dev.polek.photobrowser.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import dev.polek.photobrowser.databinding.ActivityMainBinding
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val adapter = PhotoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val photo = adapter.photos[position]
                Timber.d("Selected: $photo")
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Disable Swipe-refresh while dragging page in ViewPager
                if (!binding.swipeRefresh.isRefreshing) {
                    binding.swipeRefresh.isEnabled = state == ViewPager2.SCROLL_STATE_IDLE
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.uiState.observe(this) { state ->
            state ?: return@observe

            when (state) {
                is UiState.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                    if (state.photos != null) {
                        adapter.photos = state.photos
                    }
                }
                is UiState.Loaded -> {
                    binding.swipeRefresh.isRefreshing = false
                    adapter.photos = state.photos
                    if (state.photos.isNotEmpty()) {
                        binding.viewPager.setCurrentItem(0, false)
                    }
                }
                is UiState.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    // TODO: show error
                }
            }
        }
    }
}
