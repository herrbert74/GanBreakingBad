package com.babestudios.ganbb.di

import com.babestudios.ganbb.data.network.GanBbRepositoryContract
import com.babestudios.ganbb.data.network.characterDtoMapper
import com.babestudios.ganbb.data.network.dto.CharacterDto
import com.babestudios.ganbb.testhelpers.JsonHelper
import com.github.michaelbull.result.Ok
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mockk.coEvery
import io.mockk.mockk
import javax.inject.Singleton

@Module
@Suppress("unused")
@InstallIn(SingletonComponent::class)
class FakeNetworkModule {

    @Provides
    @Singleton
    fun provideGanBbRepository(): GanBbRepositoryContract {
        val mockkGanBbRepository =  mockk<GanBbRepositoryContract>()
        val response = JsonHelper().loadJson("bb_characters_response")
        val gson = Gson()
        val itemType = object : TypeToken<List<CharacterDto>>() {}.type
        val responseDto = gson.fromJson<List<CharacterDto>>(response, itemType)
        val mappedResponse = characterDtoMapper(responseDto)
        coEvery {
            mockkGanBbRepository.getCharacters()
        } returns Ok(mappedResponse)
        return mockkGanBbRepository
    }
}