package com.example.model

import com.example.network.model.response.UserInfoResponse
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val id: Long,
    val name: String,
    val surname: String,
    val icon: String?,
    val doctorStatus: Boolean,
    val category: String
){
    fun toUserInfoResponse() = UserInfoResponse(
        id = this.id,
        name = this.name,
        surname = this.surname,
        icon = this.icon,
        doctorStatus = this.doctorStatus,
        category = this.category)
    }

