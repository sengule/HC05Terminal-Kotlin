package com.example.kotlin_compose_bluetooth.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.kotlin_compose_bluetooth.hasPermission
import com.example.kotlin_compose_bluetooth.model.BluetoothDeviceModel
import com.example.kotlin_compose_bluetooth.model.MessageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import java.util.logging.Handler



@SuppressLint("MissingPermission")
class AppBluetoothController(val context: Context): BluetoothController {

    companion object{
        private val REQUEST_ENABLE: Int = 1111
        private val SERVER_NAME: String = "my_app"
        private val SERVER_UUID: String = "00001101-0000-1000-8000-00805f9b34fb"
    }

    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter = bluetoothManager?.adapter

    private val _pairedDeviceList = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())
    override val pairedDeviceList: StateFlow<List<BluetoothDeviceModel>>
        get() = _pairedDeviceList.asStateFlow()

    private val _isBluetoothEnabled = MutableStateFlow<Boolean>(bluetoothAdapter?.isEnabled ?: false)
    override val isBluetoothEnabled: StateFlow<Boolean>
        get() = _isBluetoothEnabled.asStateFlow()

    private val _isModuleConnected = MutableStateFlow<Boolean>(false)
    override val isModuleConnected: StateFlow<Boolean>
        get() = _isModuleConnected.asStateFlow()


    private var mmSocket: BluetoothSocket? = null
    private var bluetoothMessageService: BluetoothMessageService? = null

    private val bluetoothStateReceiver = BluetoothStateReceiver(
        onBluetoothOn = {
            _isBluetoothEnabled.value = true
            updatePairedDevices()
                        },
        onBluetoothOff = {_isBluetoothEnabled.value = false},
        onConnection = {_isModuleConnected.value = true},
        onDisconnect = {_isModuleConnected.value = false}
    )

    init {
        registerBluetoothState()

        if (hasPermission(context, Manifest.permission.BLUETOOTH_CONNECT)){
            updatePairedDevices()
        }
    }

    override fun updatePairedDevices() {
        if (!hasPermission(context, Manifest.permission.BLUETOOTH_CONNECT)){
            throw SecurityException("Bluetooth Connection permission needed")
        }

        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceModel() }
            ?.also { devices ->
                _pairedDeviceList.update { devices }
            }

    }

    override fun bluetoothEnableRequest() {
        if (bluetoothAdapter?.isEnabled == false){
            //enable request for bluetooth
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(context as (Activity), enableBtIntent, REQUEST_ENABLE, null)
        }
    }

    override fun connect(device: BluetoothDeviceModel): Flow<ConnectionResult> {
        return flow {

            if (!hasPermission(context, Manifest.permission.BLUETOOTH_CONNECT)){
                throw SecurityException("Bluetooth Connection permission needed")
            }

            bluetoothAdapter?.cancelDiscovery()

            try {
                mmSocket = bluetoothAdapter?.getRemoteDevice(device.macAddress)
                    ?.createRfcommSocketToServiceRecord(UUID.fromString(SERVER_UUID))
            } catch (e: IOException) {
                println(e.localizedMessage)
                emit(ConnectionResult.Error("There is an error"))
            }

            mmSocket?.let {socket->
                try {
                    socket.connect()
                    manageConnectedSocket(socket)
                    emit(ConnectionResult.ConnectionDone)
                }catch (e: IOException){
                    socket.close()
                    mmSocket = null
                    emit(ConnectionResult.Error(e.message + "dasda"))
                }
            }

        }.flowOn(Dispatchers.IO)
    }

    override fun startListeningModule(): Flow<MessageModel> {
        return bluetoothMessageService!!.listenModuleMessages()
    }

    override suspend fun sendMessage(bytes: ByteArray): Boolean {

        if (mmSocket?.isConnected == true){
            bluetoothMessageService?.sendMessage(bytes)
            return true
        }

        return false
    }

    override fun registerBluetoothState() {

        val intentFilter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }

        context.registerReceiver(
            bluetoothStateReceiver,
            intentFilter
        )
    }

    override fun closeConnection() {
        mmSocket?.close()
        mmSocket = null
    }

    override fun releaseBluetooth() {
        //context.unregisterReceiver(broadcastReceiver)
        //context.unregisterReceiver(bluetoothScanReceiver)
    }

    private fun manageConnectedSocket(cSocket: BluetoothSocket){
        bluetoothMessageService = BluetoothMessageService(cSocket)
    }

}

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceModel(): BluetoothDeviceModel {
    return BluetoothDeviceModel(
        name = name,
        macAddress = address
    )
}

