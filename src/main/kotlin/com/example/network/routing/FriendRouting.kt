package com.example.network.routing

import com.example.database.model.users.Friends
import com.example.database.model.users.RequestsFriends
import com.example.database.model.users.Users
import com.example.model.Friend
import com.example.model.RequestFriend
import com.example.network.model.response.FriendsListResponse
import com.example.network.model.response.FriendsRequestListResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureFriendRouting() {
    routing{
        route("/friends"){
            authenticate("jwt") {

                //get friends list
                get("/list"){

                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.getClaim("userId", Long::class)

                    if (userId == null){
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "Authentication failed: Failed to parse Access token"
                        )
                        return@get
                    }

                    val list = Friends.getFriendsListWithInfoById(userId)

                    call.respond(HttpStatusCode.OK, FriendsListResponse(
                        friendsList = list
                    ))

                }


                get("/{friendId}/create"){
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.getClaim("userId", Long::class)

                    if (userId == null){
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "Authentication failed: Failed to parse Access token"
                        )
                        return@get
                    }

                    val paramFriendId = call.parameters["friendId"]
                    if (paramFriendId == null) {
                        call.respond(HttpStatusCode.Conflict, "Param friendId is absent")
                        return@get
                    }

                    val friendId: Long
                    try {
                        friendId = paramFriendId.toLong()
                    }catch (e: Exception){
                        call.respond(HttpStatusCode.Conflict, "Invalid param friendId")
                        return@get
                    }

                    if(userId == friendId){
                        call.respond(HttpStatusCode.Conflict, "Invalid param friendId")
                        return@get
                    }

                    if(Users.getUserById(friendId) == null){
                        call.respond(HttpStatusCode.Conflict, "Invalid param friendId")
                        return@get
                    }

                    val friend = RequestsFriends.getRequest(userId, friendId)
                    if(friend != null){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    val res = RequestsFriends.insertRequest(
                        RequestFriend(
                            id = 0,
                            userId = userId,
                            friendId = friendId,
                            status = 0
                        )
                    )

                    if(res == null){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    call.respond(HttpStatusCode.Created)

                }

                //accept friend request
                get("/{requestId}/accept") {

                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.getClaim("userId", Long::class)

                    if (userId == null){
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "Authentication failed: Failed to parse Access token"
                        )
                        return@get
                    }

                    val paramRequestId = call.parameters["requestId"]
                    if (paramRequestId == null) {
                        call.respond(HttpStatusCode.Conflict, "Param requestId is absent")
                        return@get
                    }

                    val requestId: Long
                    try {
                        requestId = paramRequestId.toLong()
                    }catch (e: Exception){
                        call.respond(HttpStatusCode.Conflict, "Invalid param requestId")
                        return@get
                    }


                    val friend = RequestsFriends.getRequestById(requestId)
                    if(friend == null){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    if(friend.status != 0){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    if(userId != friend.userId || userId != friend.friendId){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    val res = RequestsFriends.acceptRequest(requestId)

                    if(res < 1){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    val friends = Friends.addFriends(
                        Friend(
                            userId = friend.userId,
                            friendId = friend.friendId
                        )
                    )

                    if(friends == null){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    call.respond(HttpStatusCode.OK)
                }

                //decline friend request
                get("/{requestId}/decline") {

                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.getClaim("userId", Long::class)

                    if (userId == null){
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "Authentication failed: Failed to parse Access token"
                        )
                        return@get
                    }

                    val paramRequestId = call.parameters["requestId"]
                    if (paramRequestId == null) {
                        call.respond(HttpStatusCode.Conflict, "Param friendId is absent")
                        return@get
                    }

                    val requestId: Long
                    try {
                        requestId = paramRequestId.toLong()
                    }catch (e: Exception){
                        call.respond(HttpStatusCode.Conflict, "Invalid param friendId")
                        return@get
                    }

                    if(RequestsFriends.getRequestById(requestId) == null){
                        call.respond(HttpStatusCode.Conflict, "Invalid param friendId")
                        return@get
                    }

                    val friend = RequestsFriends.getRequestById(requestId)
                    if(friend == null){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    if(friend.status != 0){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    val res = RequestsFriends.declineRequest(requestId)

                    if(res < 1){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    call.respond(HttpStatusCode.OK)
                }

                //delete friend
                get("/{friendId}/delete") {

                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.getClaim("userId", Long::class)

                    if (userId == null){
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "Authentication failed: Failed to parse Access token"
                        )
                        return@get
                    }

                    val paramFriendId = call.parameters["friendId"]
                    if (paramFriendId == null) {
                        call.respond(HttpStatusCode.Conflict, "Param friendId is absent")
                        return@get
                    }

                    val friendId: Long
                    try {
                        friendId = paramFriendId.toLong()
                    }catch (e: Exception){
                        call.respond(HttpStatusCode.Conflict, "Invalid param friendId")
                        return@get
                    }

                    if(Users.getUserById(friendId) == null){
                        call.respond(HttpStatusCode.Conflict, "Invalid param friendId")
                        return@get
                    }

                    val friend = Friends.getFriend(userId, friendId)
                    if(friend == null){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }


                    val res = Friends.deleteFriend(friend.userId,friend.friendId)

                    if(res < 1){
                        call.respond(HttpStatusCode.Conflict)
                        return@get
                    }

                    call.respond(HttpStatusCode.OK)
                }

                //friends requests
                route("/requests"){

                    //output friends requests
                    get("/output") {
                        val principal = call.principal<JWTPrincipal>()
                        val userId = principal?.getClaim("userId", Long::class)

                        if (userId == null){
                            call.respond(
                                HttpStatusCode.BadRequest,
                                "Authentication failed: Failed to parse Access token"
                            )
                            return@get
                        }

                        val list = RequestsFriends.getOutputRequestList(userId)

                        call.respond(
                            HttpStatusCode.OK,
                            FriendsRequestListResponse(
                                friendsList = list
                            )
                        )
                    }

                    //input friends requests
                    get("/input") {
                        val principal = call.principal<JWTPrincipal>()
                        val userId = principal?.getClaim("userId", Long::class)

                        if (userId == null){
                            call.respond(
                                HttpStatusCode.BadRequest,
                                "Authentication failed: Failed to parse Access token"
                            )
                            return@get
                        }

                        val list = RequestsFriends.getInputRequestList(userId)

                        call.respond(
                            HttpStatusCode.OK,
                            FriendsRequestListResponse(
                                friendsList = list
                            )
                        )
                    }
                }

            }
        }
    }
}