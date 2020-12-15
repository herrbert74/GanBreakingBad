package com.babestudios.ganbb.di

import android.content.Context
import com.babestudios.ganbb.Database
import com.babestudios.ganbb.data.local.DatabaseContract
import com.babestudios.ganbb.data.local.SqlDelightDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@Suppress("unused")
@InstallIn(SingletonComponent::class)
class LocalModule {
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

}