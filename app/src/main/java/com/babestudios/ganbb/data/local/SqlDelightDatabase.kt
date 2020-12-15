package com.babestudios.ganbb.data.local

import com.babestudios.blockchain.CharactersQueries
import com.babestudios.ganbb.Database
import com.babestudios.ganbb.data.network.dto.Status
import com.babestudios.ganbb.model.Character
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SqlDelightDatabase @Inject constructor(private val database: Database) : DatabaseContract {
    override fun saveCharactersResponse(characters: List<Character>) {
        val charactersQueries: CharactersQueries = database.charactersQueries
        charactersQueries.deleteAll()
        characters.forEach {
            charactersQueries.insertCharacter(
                it.id,
                it.name,
                it.occupation,
                it.img,
                it.status.value,
                it.nickname,
                it.appearance
            )
        }
    }

    override fun getCharactersResponse(): List<Character> {
        val charactersQueries: CharactersQueries = database.charactersQueries
        return charactersQueries
            .selectAll { id, name, occupation, img, status, nickname, appearance ->
                Character(
                    id, name, occupation, img, Status.fromValue(status), nickname, appearance
                )
            }.executeAsList()
    }
}