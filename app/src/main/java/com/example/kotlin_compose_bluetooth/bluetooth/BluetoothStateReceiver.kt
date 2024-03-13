package com.example.kotlin_compose_bluetooth.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothStateReceiver(
    private val onBluetoothOn: () -> Unit,
    private val onBluetoothOff: () -> Unit,
    private val onConnection: () -> Unit,
    private val onDisconnect: () -> Unit
): BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        when(action){
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    -1
                )

                when(state){
                    BluetoothAdapter.STATE_OFF ->{
                        onBluetoothOff()
                    }

                    BluetoothAdapter.STATE_ON ->{
                        onBluetoothOn()
                    }
                }

            }

            BluetoothDevice.ACTION_ACL_CONNECTED ->{
                onConnection()
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED ->{
                onDisconnect()
            }

        }

    }
}