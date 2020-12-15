package com.babestudios.ganbb.data.network

import com.babestudios.ganbb.data.network.dto.CharacterDto
import retrofit2.http.GET

interface GanBbService {
    @GET("api/characters")
    suspend fun getCharacters(): List<CharacterDto>
}