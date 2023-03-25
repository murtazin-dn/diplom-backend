package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class EmailTakenResponse(
    val isTaken: Boolean
)
