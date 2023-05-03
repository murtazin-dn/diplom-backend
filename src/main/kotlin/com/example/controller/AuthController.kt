package com.example.controller

import com.example.database.model.Users
import com.example.model.User
import com.example.network.model.HttpResponse
import com.example.network.model.request.SignInRequest
import com.example.network.model.request.SignUpRequest
import com.example.network.model.response.AuthResponse
import com.example.network.model.response.EmailTakenResponse
import com.example.secure.JwtTokenService
import com.example.secure.PasswordEncryptor
import com.example.utils.UnauthorizedActivityException
import com.example.utils.isEmailValid
import com.example.utils.isNameValid
import io.ktor.server.application.*
import io.ktor.server.plugins.*

class AuthControllerImpl: AuthController {
    override suspend fun signUp(signUpRequest: SignUpRequest): HttpResponse<AuthResponse> {
        return try{
            validateSignUpFieldsOrThrowException(signUpRequest)
            isUserExist(signUpRequest.email)
            val hashPass = PasswordEncryptor.encryptPassword(signUpRequest.password)
            Users.insert(
                User(
                    id = 0,
                    password = hashPass,
                    email = signUpRequest.email,
                    name = signUpRequest.name,
                    surname = signUpRequest.surname,
                    dateOfBirthday = signUpRequest.dateOfBirthday,
                    categoryId = signUpRequest.categoryId,
                    doctorStatus = false,
                    icon = null
                )
            )?.let { user ->
                val token = JwtTokenService.generate(user.id)
                HttpResponse.ok(AuthResponse(token))
            } ?: throw BadRequestException("Insertion failed")

        } catch (e: BadRequestException){
            HttpResponse.badRequest(e.message.toString())
        }
    }
    private fun validateSignUpFieldsOrThrowException(
        signUpRequest: SignUpRequest
    ) {
        val message = when {
            (signUpRequest.email.isBlank() or (signUpRequest.name.isBlank()) or
                    (signUpRequest.password.isBlank()) or (signUpRequest.confirmPassword.isBlank()) or
                    (signUpRequest.surname.isBlank())) -> "Fields should not be blank"
            (!signUpRequest.email.isEmailValid()) -> "Email invalid"
            (!signUpRequest.name.isNameValid()) -> "No special characters allowed in name"
            (!signUpRequest.surname.isNameValid()) -> "No special characters allowed in surname"
            (signUpRequest.password.length !in (8..50)) -> "Password should be of min 8 and max 50 character in length"
            (signUpRequest.confirmPassword.length !in (8..50)) -> "Password should be of min 8 and max 50 character in length"
            (signUpRequest.password != signUpRequest.confirmPassword) -> "Passwords do not match"
            else -> return
        }

        throw BadRequestException(message)
    }
    private suspend fun isUserExist(email: String) {
        if(Users.getUserByEmail(email) != null) throw BadRequestException("user with this email already exists")
    }

    override suspend fun signIn(signInRequest: SignInRequest): HttpResponse<AuthResponse> {
        return try{
            validateSignInFieldsOrThrowException(signInRequest)
            Users.getUserByEmail(signInRequest.email)?.let {user ->
                if (!PasswordEncryptor.validatePassword(signInRequest.password,user.password))
                    throw UnauthorizedActivityException("Authentication failed: Invalid credentials")
                val token = JwtTokenService.generate(user.id)
                HttpResponse.ok(AuthResponse(token))
            } ?: throw UnauthorizedActivityException("Authentication failed: Invalid credentials")
        } catch (e: BadRequestException){
            HttpResponse.badRequest(e.message.toString())
        } catch (e: UnauthorizedActivityException){
            HttpResponse.unauth(e.message)
        }
    }

    override suspend fun findEmail(call: ApplicationCall): HttpResponse<EmailTakenResponse> {
        return try{
            val email = call.parameters["email"] ?: throw BadRequestException("Param email is exists")
            if(!email.isEmailValid()) throw BadRequestException("Invalid param email")
            Users.getUserByEmail(email)?.let {
                HttpResponse.ok(EmailTakenResponse(true))
            } ?: HttpResponse.ok(EmailTakenResponse(false))
        } catch (e: BadRequestException){
            HttpResponse.badRequest(e.message.toString())
        } catch (e: Exception){
            HttpResponse.badRequest(e.message.toString())
        }
    }

    private fun validateSignInFieldsOrThrowException(
        signInRequest: SignInRequest
    ) {
        val message = when {
            (signInRequest.email.isBlank() or (signInRequest.password.isBlank())) -> "Credentials fields should not be blank"
            (!signInRequest.email.isEmailValid()) -> "Email invalid"
            (signInRequest.password.length !in (8..50)) -> "Password should be of min 8 and max 50 character in length"

            else -> return
        }

        throw BadRequestException(message)
    }


    private fun validateDateOfBirthday(dateOfBirthday: Long){
        TODO()
    }
}
interface AuthController {
    suspend fun signUp(signUpRequest: SignUpRequest): HttpResponse<AuthResponse>
    suspend fun signIn(signInRequest: SignInRequest): HttpResponse<AuthResponse>
    suspend fun findEmail(call: ApplicationCall): HttpResponse<EmailTakenResponse>
}