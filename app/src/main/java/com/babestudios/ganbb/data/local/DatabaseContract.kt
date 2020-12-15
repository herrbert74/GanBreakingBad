package com.babestudios.ganbb.data.local

import com.babestudios.ganbb.model.Character

interface DatabaseContract {
    fun saveCharactersResponse(characters: @JvmSuppressWildcards List<Character>)
    fun getCharactersResponse(): List<Character>
}