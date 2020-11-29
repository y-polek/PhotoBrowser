package dev.polek.photobrowser.di

import android.app.Activity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    fun glideRequestManager(activity: Activity): RequestManager {
        return Glide.with(activity)
    }
}
