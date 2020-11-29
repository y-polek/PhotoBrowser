package dev.polek.photobrowser.di

import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.polek.photobrowser.BuildConfig
import dev.polek.photobrowser.api.ApiKeyInterceptor
import dev.polek.photobrowser.api.FlickrService
import dev.polek.photobrowser.repository.FlickrRepository
import dev.polek.photobrowser.repository.FlickrRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun okHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .setLevel(if (BuildConfig.DEBUG) Level.BODY else Level.BASIC)
    }

    @Singleton
    @Provides
    fun okHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun moshi(): Moshi {
        return Moshi.Builder()
            .add(Wrapped.ADAPTER_FACTORY)
            .build()
    }

    @Singleton
    @Provides
    fun flickrService(client: OkHttpClient, moshi: Moshi): FlickrService {
        return Retrofit.Builder()
            .baseUrl("https://api.flickr.com/services/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FlickrService::class.java)
    }

    @Singleton
    @Provides
    fun flickrRepository(impl: FlickrRepositoryImpl): FlickrRepository = impl
}
