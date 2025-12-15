package com.example.nextrequest.home.presentation.util

import retrofit2.HttpException
import java.net.*
import java.nio.channels.UnresolvedAddressException

fun Exception.getNetworkErrorMessage(): String {
    return when (this) {
        is UnknownHostException -> "Unknown Host"
        is SocketTimeoutException -> "Connection timed out"
        is ConnectException -> "Couldn't connect to the server"
        is UnresolvedAddressException -> "The host cannot be reached. Please Check the URL or your internet connection"
        is HttpException -> {
            val code = this.code()
            when (code) {
                400 -> "Bad request."
                401 -> "You are not authorized."
                403 -> "Access denied."
                404 -> "Not found."
                405 -> "This operation isn’t allowed."
                500 -> "Server error. Please try again later."
                else -> "HTTP error: $code"
            }
        }
        else -> this.localizedMessage ?: "An unknown error occurred"
    }
}