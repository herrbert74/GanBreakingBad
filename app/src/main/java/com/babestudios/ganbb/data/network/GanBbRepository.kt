package com.babestudios.ganbb.data.network

import com.babestudios.ganbb.common.network.OfflineException
import com.babestudios.ganbb.data.local.DatabaseContract
import com.babestudios.ganbb.data.network.dto.CharacterDto
import com.babestudios.ganbb.data.network.dto.mapAppearance
import com.babestudios.ganbb.data.network.dto.mapCharacterDto
import com.babestudios.ganbb.data.network.dto.mapOccupation
import com.babestudios.ganbb.model.Character
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import javax.inject.Singleton

@Singleton
class GanBbRepository(
    private val ganBbService: GanBbService,
    private val database: DatabaseContract
) : GanBbRepositoryContract {
    override suspend fun getCharacters(): Result<List<Character>, Throwable> {
        return runCatching {
            characterDtoMapper(ganBbService.getCharacters()).also {
                database.saveCharactersResponse(it)
            }
        }.mapError { OfflineException(database.getCharactersResponse()) }
    }
}

fun characterDtoMapper(list: List<CharacterDto>): List<Character> =
    list.map { characterDto ->
        mapCharacterDto(
            characterDto,
            ::mapOccupation,
            ::mapAppearance,
        )
    }
