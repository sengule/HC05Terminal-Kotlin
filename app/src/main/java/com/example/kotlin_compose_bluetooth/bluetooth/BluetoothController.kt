package com.example.kotlin_compose_bluetooth.bluetooth

import com.example.kotlin_compose_bluetooth.model.BluetoothDeviceModel
import com.example.kotlin_compose_bluetooth.model.MessageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.InputStream
import java.io.OutputStream

/*
    Bluetooth Controller for HC-05 module
 */
interface BluetoothController {
    val pairedDeviceList : StateFlow<List<BluetoothDeviceModel>>
    val isBluetoothEnabled: StateFlow<Boolean>
    val isModuleConnected: StateFlow<Boolean>
    fun bluetoothEnableRequest()
    fun updatePairedDevices()
    fun connect(device: BluetoothDeviceModel): Flow<ConnectionResult>
    fun startListeningModule(): Flow<MessageModel>
    suspend fun sendMessage(bytes: ByteArray): Boolean
    fun registerBluetoothState()
    fun closeConnection()
    fun releaseBluetooth()
}