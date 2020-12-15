package com.babestudios.ganbb.data.network.dto

import com.babestudios.ganbb.model.Character

inline fun mapCharacterDto(
    input: CharacterDto,
    occupationMapper: (List<String>) -> String,
    appearanceMapper: (List<Int>?) -> String
): Character {
    return Character(
        input.char_id,
        input.name,
        occupationMapper(input.occupation),
        input.img ?: "",
        input.status ?: Status.Unknown,
        input.nickname ?: "",
        appearanceMapper(input.appearance)
    )
}

fun mapOccupation(occupationList: List<String>): String {
    return occupationList.joinToString(", ")
}

fun mapAppearance(appearanceList: List<Int>?): String {
    return appearanceList?.joinToString(", ") ?: ""
}
