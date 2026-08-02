package com.example.moil.core.domain

import com.example.moil.core.network.NetworkResult

fun <T, R> NetworkResult<T>.mapToDomain(mapper: (T) -> R): MoilResult<R> = when (this) {
    is NetworkResult.Success -> MoilResult.Success(mapper(data))
    is NetworkResult.ServerError -> MoilResult.Failure(MoilError.Server(status, message))
    is NetworkResult.HttpError -> MoilResult.Failure(MoilError.Http(code, message))
    is NetworkResult.NetworkError -> MoilResult.Failure(MoilError.Network)
}
