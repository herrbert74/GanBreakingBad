package com.babestudios.ganbb.di

import com.babestudios.ganbb.common.mappers.mapNullInputList
import com.babestudios.ganbb.data.network.dto.CharacterDto
import com.babestudios.ganbb.data.network.dto.mapAppearance
import com.babestudios.ganbb.data.network.dto.mapCharacterDto
import com.babestudios.ganbb.data.network.dto.mapOccupation
import com.babestudios.ganbb.model.Character
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Not used for now because there is a compile problem in Kotlin for Lists.
 * Instead it's defined directly in the repository class.
 */
@Module
@InstallIn(SingletonComponent::class)
class MapperModule {
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
