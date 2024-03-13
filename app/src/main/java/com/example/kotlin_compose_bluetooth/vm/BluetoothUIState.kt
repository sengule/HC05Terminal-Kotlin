package com.example.kotlin_compose_bluetooth.vm

import com.example.kotlin_compose_bluetooth.model.BluetoothDeviceModel
import com.example.kotlin_compose_bluetooth.model.MessageModel

data class BluetoothUIState(
    val pairedDevices: List<BluetoothDeviceModel> = emptyList(),
    val isEnabled: Boolean = false,
    val isConnected: Boolean = false,
    val messages: List<MessageModel> = emptyList()
)
