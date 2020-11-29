package dev.polek.photobrowser.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.polek.photobrowser.BuildConfig
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

        setupImagePreload(imageRequestManager)

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
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupImagePreload(imageRequestManager: RequestManager) {
        if (!BuildConfig.ENABLE_FEATURE_IMAGE_PRELOAD) return

        val recyclerView = binding.viewPager.recyclerView() ?: return

        val preloadModelProvider = object : ListPreloader.PreloadModelProvider<String> {
            override fun getPreloadItems(position: Int): List<String> {
                return listOf(adapter.photos[position].url)
            }

            override fun getPreloadRequestBuilder(item: String): RequestBuilder<*> {
                return imageRequestManager.load(item).centerInside()
            }

        }
        val preloadSizeProvider = ViewPreloadSizeProvider<String>(binding.viewPager)
        val preloader = RecyclerViewPreloader(
            imageRequestManager,
            preloadModelProvider,
            preloadSizeProvider,
            IMAGE_PRELOAD_SIZE
        )
        recyclerView.addOnScrollListener(preloader)
    }

    private companion object {

        private const val IMAGE_PRELOAD_SIZE = 4

        /**
         * This extension function uses reflection to access `RecyclerView` field of `ViewPager2`
         */
        private fun ViewPager2.recyclerView(): RecyclerView? {
            return try {
                val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
                recyclerViewField.isAccessible = true
                val recyclerView = recyclerViewField.get(this) as? RecyclerView
                if (recyclerView == null) {
                    Timber.e("RecyclerView field not found")
                }
                recyclerView
            } catch (e: NoSuchFieldException) {
                Timber.e(e, "RecyclerView field not found")
                null
            }
        }
    }
}
