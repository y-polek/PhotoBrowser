package dev.polek.photobrowser.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dev.polek.photobrowser.databinding.ActivityMainBinding
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupSwipeRefresh()

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

    private fun setupViewPager() {
        val imageRequestManager = Glide.with(this)
        adapter = PhotoAdapter(imageRequestManager)

        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 3
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
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.swipeRefresh.setOnApplyWindowInsetsListener { _, insets ->
            val isRefreshing = binding.swipeRefresh.isRefreshing

            val topInset = insets.systemWindowInsetTop
            binding.swipeRefresh.setProgressViewOffset(false, 0, topInset)

            binding.swipeRefresh.isRefreshing = isRefreshing
            return@setOnApplyWindowInsetsListener insets
        }
    }
}
