package me.quenchjian.migotest.network

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.time.Duration

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

  companion object {

    @Provides
    fun provideHttpClient(): OkHttpClient {
      return OkHttpClient.Builder()
        .connectTimeout(Duration.ofSeconds(30))
        .readTimeout(Duration.ofSeconds(30))
        .writeTimeout(Duration.ofSeconds(30))
        .build()
    }
  }

  @Binds
  abstract fun bindRestfulApi(api: OkHttpApi): RestfulApi
}