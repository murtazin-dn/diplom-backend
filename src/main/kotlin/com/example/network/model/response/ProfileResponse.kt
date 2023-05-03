package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val profile: UserInfoResponse,
    val isSubscribe: Boolean
)
