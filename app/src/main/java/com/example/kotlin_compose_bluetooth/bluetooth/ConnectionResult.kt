package com.example.kotlin_compose_bluetooth.bluetooth

sealed interface ConnectionResult {
    data object ConnectionEmpty: ConnectionResult
    data object ConnectionDone: ConnectionResult
    data class Error(val message: String): ConnectionResult
}