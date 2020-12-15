package com.babestudios.ganbb.di

import android.content.Context
import android.net.ConnectivityManager
import com.babestudios.ganbb.BuildConfig
import com.babestudios.ganbb.Database
import com.babestudios.ganbb.common.mappers.mapNullInputList
import com.babestudios.ganbb.data.local.DatabaseContract
import com.babestudios.ganbb.data.local.SqlDelightDatabase
import com.babestudios.ganbb.data.network.GanBbRepository
import com.babestudios.ganbb.data.network.GanBbRepositoryContract
import com.babestudios.ganbb.data.network.GanBbService
import com.babestudios.ganbb.data.network.dto.CharacterDto
import com.babestudios.ganbb.data.network.dto.mapAppearance
import com.babestudios.ganbb.data.network.dto.mapCharacterDto
import com.babestudios.ganbb.data.network.dto.mapOccupation
import com.babestudios.ganbb.data.res.StringResourceHelper
import com.babestudios.ganbb.data.res.StringResourceHelperContract
import com.babestudios.ganbb.model.Character
import com.babestudios.ganbb.navigation.GanBbNavigation
import com.babestudios.ganbb.navigation.GanBbNavigator
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@Suppress("unused")
@InstallIn(SingletonComponent::class)
class SingletonModule {
    @Suppress("DEPRECATION")
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

    @Provides
    @Singleton
    fun provideLogoQuizNavigator(): GanBbNavigator {
        return GanBbNavigation()
    }

    @Provides
    @Singleton
    fun provideStringResourceHelper(
        @ApplicationContext context: Context
    ): StringResourceHelperContract {
        return StringResourceHelper(context)
    }

    @Provides
    @Singleton
    internal fun provideDatabaseContract(database: Database): DatabaseContract {
        return SqlDelightDatabase(database)
    }

    @Provides
    @Singleton
    internal fun provideDatabase(driver: AndroidSqliteDriver): Database {
        return Database(driver)
    }

    @Provides
    @Singleton
    internal fun provideSqlDriver(
        @ApplicationContext context: Context
    ): AndroidSqliteDriver {
        return AndroidSqliteDriver(Database.Schema, context, "Characters.db")
    }

    @Provides
    @Singleton
    internal fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    fun provideCharactersDtoMapper(): (List<CharacterDto>) -> List<Character> =
        { list ->
            mapNullInputList(list) { characterDto ->
                mapCharacterDto(
                    characterDto,
                    ::mapOccupation,
                    ::mapAppearance,
                )
            }
        }

}