package com.example.kotlin_compose_bluetooth.viewmodels

import android.os.Message
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin_compose_bluetooth.BluetoothUIState
import com.example.kotlin_compose_bluetooth.SERVER_NAME
import com.example.kotlin_compose_bluetooth.bluetooth.BluetoothController
import com.example.kotlin_compose_bluetooth.bluetooth.ConnectionResult
import com.example.kotlin_compose_bluetooth.model.BluetoothDeviceModel
import com.example.kotlin_compose_bluetooth.model.MessageModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BluetoothViewModel(
    val bluetoothController: BluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUIState())
    val state = combine(
        bluetoothController.scannedDeviceList,
        bluetoothController.pairedDeviceList,
        _state
    ){ scannedDeviceList, pairedDeviceList, state ->
        state.copy(
            scannedDevices = scannedDeviceList,
            pairedDevices = pairedDeviceList
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    //private val _connectionState = MutableStateFlow<ConnectionResult>(ConnectionResult.ConnectionEmpty) // if connection is established this will be connection done
    //val connectionState: StateFlow<ConnectionResult> = _connectionState.asStateFlow()

    val isScanning: StateFlow<Boolean>
        get() = bluetoothController.isScanning

    fun startScan(){
        bluetoothController.resetScannedDeviceList()
        bluetoothController.scanBluetoothDevices()
    }

    fun stopScan(){
        bluetoothController.stopScan()
    }

    fun connect(device: BluetoothDeviceModel){

        val connectionFlow = bluetoothController.connect(device)
        viewModelScope.launch {
            connectionFlow.collect{state->
                when(state){
                    ConnectionResult.ConnectionDone -> {
                        println("Connection is successful. Done with ${device.name}")
                        //_connectionState.value = ConnectionResult.ConnectionDone
                        _state.update { it.copy(isConnected = true) }
                        listenModuleMessages()
                    }
                    is ConnectionResult.Error -> println("Connection failed due to ${state.message}")
                    ConnectionResult.ConnectionEmpty -> println("Connection is empty now")
                }
            }
        }

    }

    fun close(){
        bluetoothController.closeConnection()
    }

    fun sendMessage(message: String){
        viewModelScope.launch{
            val isMessageSent = bluetoothController.sendMessage(message.toByteArray())

            if (isMessageSent){
                val messageModel = MessageModel(sender = SERVER_NAME, message = message)
                _state.update {
                    it.copy(
                        messages = it.messages + messageModel
                    )
                }
            }

        }
    }

    private fun listenModuleMessages(){
        val messageFlow = bluetoothController.startListeningModule()

        viewModelScope.launch {
            messageFlow.collect{message->
                _state.update {
                    it.copy(messages = it.messages + message)
                }
            }
        }
    }


    companion object{
        fun provideFactory(
            bluetoothController: BluetoothController
        ): ViewModelProvider.Factory{

            val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BluetoothViewModel(
                        bluetoothController
                    ) as T
                }
            }

            return factory
        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.releaseBluetooth()
    }


    /*
       private fun Flow<ConnectionResult>.listen(): Job {
        return onEach {result->

            when(result){
                is ConnectionResult.ConnectionDone -> {
                    println("Connection is done")
                }
                is ConnectionResult.Error -> {
                    println("There is a error ${result.message}")
                }

                ConnectionResult.ConnectionEmpty -> {}
            }

        }.catch {
            bluetoothController.closeConnection()
            println("Error occured in listening:  ${it.localizedMessage}")
        }.launchIn(viewModelScope)
    }


     */

}