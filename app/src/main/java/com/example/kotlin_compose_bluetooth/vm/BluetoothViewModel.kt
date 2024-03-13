package com.example.kotlin_compose_bluetooth.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin_compose_bluetooth.SERVER_NAME
import com.example.kotlin_compose_bluetooth.bluetooth.BluetoothController
import com.example.kotlin_compose_bluetooth.bluetooth.ConnectionResult
import com.example.kotlin_compose_bluetooth.model.BluetoothDeviceModel
import com.example.kotlin_compose_bluetooth.model.MessageModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BluetoothViewModel(
    val bluetoothController: BluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUIState())
    val state = combine(
        bluetoothController.pairedDeviceList,
        bluetoothController.isBluetoothEnabled,
        bluetoothController.isModuleConnected,
        _state
    ){ pairedDeviceList, isBluetoothEnabled, isModuleConnected,state ->
        state.copy(
            pairedDevices = pairedDeviceList,
            isEnabled = isBluetoothEnabled,
            isConnected = isModuleConnected
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)


    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean>
        get() = _isConnecting.asStateFlow()

    fun updatePairedDevices(){
        bluetoothController.updatePairedDevices()
    }

    fun connect(device: BluetoothDeviceModel){

        _isConnecting.value = true

        val connectionFlow = bluetoothController.connect(device)
        viewModelScope.launch {
            connectionFlow.collect{state->
                when(state){
                    ConnectionResult.ConnectionDone -> {
                        println("Connection is successful. Done with ${device.name}")
                        //_connectionState.value = ConnectionResult.ConnectionDone
                        _state.update { it.copy(isConnected = true) }
                        _isConnecting.value = false
                        listenModuleMessages()
                    }
                    is ConnectionResult.Error -> {
                        println("Connection failed due to ${state.message}")
                        _isConnecting.value = false
                    }
                    ConnectionResult.ConnectionEmpty -> {
                        println("Connection is empty now")
                        _isConnecting.value = false
                    }
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

    fun bluetoothEnableRequest(){
        bluetoothController.bluetoothEnableRequest()
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

}