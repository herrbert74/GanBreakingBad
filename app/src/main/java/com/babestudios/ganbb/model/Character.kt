package com.babestudios.ganbb.model

import com.babestudios.ganbb.data.network.dto.Status

data class Character(
    val id: Long = 0L,
    val name: String = "",
    val occupation: String = "",
    val img: String = "",
    val status: Status = Status.Unknown,
    val nickname: String = "",
    val appearance: String = "",
)
