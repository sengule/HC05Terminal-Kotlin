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

    val scannedDeviceList: StateFlow<List<BluetoothDeviceModel>>
    val pairedDeviceList : StateFlow<List<BluetoothDeviceModel>>
    val isScanning: StateFlow<Boolean>

    fun resetScannedDeviceList()
    fun bluetoothEnableRequest()
    fun scanBluetoothDevices()
    fun updatePairedDevices()
    fun stopScan()

    fun connect(device: BluetoothDeviceModel): Flow<ConnectionResult>

    fun startListeningModule(): Flow<MessageModel>
    suspend fun sendMessage(bytes: ByteArray): Boolean

    fun closeConnection()
    fun releaseBluetooth()


}