package com.babestudios.ganbb.data.network.dto

data class CharacterDto(
    val char_id: Long,
    val name: String,
    val birthday: String?,
    val occupation: List<String>,
    val img: String?,
    val status: Status?,
    val nickname: String?,
    val appearance: List<Int>? = null,
    val portrayed: String?,
    val category: String?,
    val better_call_saul_appearance: List<Int>?
)

enum class Status(val value: String) {
    Alive("Alive"),
    Deceased("Deceased"),
    PresumedDead("Presumed dead"),
    Unknown("Unknown");

    companion object {
        public fun fromValue(value: String): Status = when (value) {
            "Alive" -> Alive
            "Deceased" -> Deceased
            "Presumed dead" -> PresumedDead
            "Unknown" -> Unknown
            else -> throw IllegalArgumentException()
        }
    }
}
