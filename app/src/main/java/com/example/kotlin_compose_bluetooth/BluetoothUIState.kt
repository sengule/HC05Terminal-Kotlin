package com.example.kotlin_compose_bluetooth

import com.example.kotlin_compose_bluetooth.model.BluetoothDeviceModel
import com.example.kotlin_compose_bluetooth.model.MessageModel

data class BluetoothUIState(
    val scannedDevices: List<BluetoothDeviceModel> = emptyList(),
    val pairedDevices: List<BluetoothDeviceModel> = emptyList(),
    val isConnected: Boolean = false,
    val messages: List<MessageModel> = emptyList()
)
