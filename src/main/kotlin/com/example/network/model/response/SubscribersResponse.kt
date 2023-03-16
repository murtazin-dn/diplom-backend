package com.example.network.model.response

import com.example.model.UserInfo
import kotlinx.serialization.Serializable

@Serializable
data class SubscribersResponse(
    val list: List<UserInfo>
)
