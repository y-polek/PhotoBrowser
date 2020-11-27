package dev.polek.photobrowser.api

import dev.polek.photobrowser.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.newBuilder()
            .setQueryParameter("api_key", BuildConfig.FLICKR_API_KEY)
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}
