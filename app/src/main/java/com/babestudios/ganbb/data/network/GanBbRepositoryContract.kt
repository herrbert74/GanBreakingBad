package com.babestudios.ganbb.data.network

import com.babestudios.ganbb.model.Character
import com.github.michaelbull.result.Result

interface GanBbRepositoryContract {
    suspend fun getCharacters(): Result<List<Character>, Throwable>
}