package com.babestudios.ganbb.di

import com.babestudios.ganbb.BuildConfig
import com.babestudios.ganbb.data.local.DatabaseContract
import com.babestudios.ganbb.data.network.GanBbRepository
import com.babestudios.ganbb.data.network.GanBbRepositoryContract
import com.babestudios.ganbb.data.network.GanBbService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@Suppress("unused")
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    internal fun provideGanBbRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor(logging)
        return Retrofit.Builder()//
            .baseUrl(BuildConfig.GAN_BB_BASE_URL)//
            .addConverterFactory(GsonConverterFactory.create())//
            .client(httpClient.build())//
            .build()
    }

    @Provides
    @Singleton
    internal fun provideGanBbService(retroFit: Retrofit): GanBbService {
        return retroFit.create(GanBbService::class.java)
    }

    @Provides
    @Singleton
    fun provideGanBbRepository(
        ganBbService: GanBbService,
        database: DatabaseContract
    ): GanBbRepositoryContract {
        return GanBbRepository(ganBbService, database)
    }
}