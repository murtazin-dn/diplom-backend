package com.example.network.model.response

import com.example.model.PersonInfo
import kotlinx.serialization.Serializable

@Serializable
data class FriendsListResponse(
    val friendsList: List<PersonInfo>
)
