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


/*
TODO: Complete all methods +
TODO: handle write and read methods
 */

@SuppressLint("MissingPermission")
class AppBluetoothController(val context: Context): BluetoothController {

    companion object{
        private val REQUEST_ENABLE: Int = 1111
        private val SERVER_NAME: String = "my_app"
        private val SERVER_UUID: String = "00001101-0000-1000-8000-00805f9b34fb"

    }

    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter = bluetoothManager?.adapter

    private val _scannedDeviceList = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())
    override val scannedDeviceList: StateFlow<List<BluetoothDeviceModel>>
        get() = _scannedDeviceList.asStateFlow()

    private val _pairedDeviceList = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())
    override val pairedDeviceList: StateFlow<List<BluetoothDeviceModel>>
        get() = _pairedDeviceList

    private val _isScanning = MutableStateFlow<Boolean>(false)
    override val isScanning: StateFlow<Boolean>
        get() = _isScanning.asStateFlow()

    private val broadcastReceiver = DeviceFounderReceiver{device->
        _scannedDeviceList.update {devices->
            val foundedDevice = device.toBluetoothDeviceModel()
            if (foundedDevice in devices) devices else devices + foundedDevice
        }
    }

    private val bluetoothScanReceiver = BluetoothScanReceiver{
        _isScanning.value = false
        println("Scanned done")
    }

    private var mmSocket: BluetoothSocket? = null
    private var bluetoothMessageService: BluetoothMessageService? = null

    init {
        updatePairedDevices()
    }

    override fun scanBluetoothDevices() {
        if (bluetoothAdapter == null)
            return

        if (!bluetoothAdapter.isEnabled){
            //_isScanning.value = false
            bluetoothEnableRequest()
        }else{
            context.registerReceiver(
                broadcastReceiver,
                IntentFilter(BluetoothDevice.ACTION_FOUND)
            )

            context.registerReceiver(
                bluetoothScanReceiver,
                IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            )

            _isScanning.value = true
            bluetoothAdapter.startDiscovery()

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

    override fun stopScan() {
        println(bluetoothAdapter!!.isDiscovering)

        if(bluetoothAdapter!!.isDiscovering){
            _isScanning.value = false
            releaseBluetooth()
            bluetoothAdapter.cancelDiscovery()
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

    override fun closeConnection() {
        mmSocket?.close()
        mmSocket = null
    }

    override fun releaseBluetooth() {
        //context.unregisterReceiver(broadcastReceiver)
        //context.unregisterReceiver(bluetoothScanReceiver)
    }

    override fun resetScannedDeviceList(){
        _scannedDeviceList.value = emptyList()

    }

    private fun manageConnectedSocket(cSocket: BluetoothSocket){
        bluetoothMessageService = BluetoothMessageService(cSocket)
    }


    /*

    private inner class ConnectThread(device: BluetoothDeviceModel): Thread(){
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.getRemoteDevice(device.macAddress)
                ?.createRfcommSocketToServiceRecord(UUID.fromString(SERVER_UUID))
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.


                try {
                    socket.connect()
                    println("Connection is succesful")
                }catch (e: IOException){
                    println(e.message + "123123")

                    try {
                        mmSocket!!.close()
                    } catch (e2: IOException) {

                        println("unable to close() socket during connection failure ${e2.message}", )
                    }

                }

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.

            }
        }

        fun cancel(){
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                println(e.message + "dasda")
            }
        }
    }
     */


}

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceModel(): BluetoothDeviceModel {
    return BluetoothDeviceModel(
        name = name,
        macAddress = address
    )
}

