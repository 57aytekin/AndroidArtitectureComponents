package com.example.yeniappwkotlin.data.network.repositories

import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest
import com.example.yeniappwkotlin.data.network.responses.AuthResponse

class UserRepository(
    private val api : MyApi
) : SafeApiRequest() {

    suspend fun userLogin(email: String, password: String): AuthResponse{
        return apiRequest { api.userLogin(email, password) }
    }
    suspend fun userRegister(name : String, email: String, password: String) : AuthResponse{
        return apiRequest { api.userRegister(name, email, password) }
    }
}