package me.quenchjian.migotest.network

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient

@Module
@TestInstallIn(
  components = [SingletonComponent::class],
  replaces = [NetworkModule::class]
)
abstract class NetworkModuleTest {

  companion object {

    @Provides
    fun provideHttpClient(): OkHttpClient {
      return OkHttpClient.Builder()
        .addInterceptor { chain ->
          val url = chain.request().url.newBuilder()
            .scheme("http").host("localhost").port(8080)
            .build()
          val req = chain.request().newBuilder().url(url).build()
          chain.proceed(req)
        }
        .build()
    }
  }

  @Binds
  abstract fun bindRestfulApi(api: OkHttpApi): RestfulApi
}