package com.babestudios.ganbb.di

import android.content.Context
import android.net.ConnectivityManager
import com.babestudios.ganbb.data.res.StringResourceHelper
import com.babestudios.ganbb.data.res.StringResourceHelperContract
import com.babestudios.ganbb.navigation.GanBbNavigation
import com.babestudios.ganbb.navigation.GanBbNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@Suppress("unused")
@InstallIn(SingletonComponent::class)
class SingletonModule {


    @Provides
    @Singleton
    fun provideGanBbNavigator(): GanBbNavigator {
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
    internal fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

}