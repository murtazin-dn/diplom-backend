package com.example.network.model.response

import com.example.model.PersonInfo
import com.example.model.PersonInfoWithRequestStatus
import kotlinx.serialization.Serializable

@Serializable
data class FriendsRequestListResponse(
    val friendsList: List<PersonInfoWithRequestStatus>
)
