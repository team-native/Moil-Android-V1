package com.example.moil.core.domain

sealed interface MoilResult<out T> {
    data class Success<T>(val value: T) : MoilResult<T>
    data class Failure(val error: MoilError) : MoilResult<Nothing>
}

sealed interface MoilError {
    data class Server(val status: Int, val message: String) : MoilError
    data class Http(val code: Int, val message: String) : MoilError
    data class Configuration(val message: String) : MoilError
    data object Network : MoilError
}
