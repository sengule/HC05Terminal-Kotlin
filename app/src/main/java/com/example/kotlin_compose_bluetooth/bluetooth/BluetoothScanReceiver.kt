package com.example.kotlin_compose_bluetooth.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothScanReceiver(
    private val onScanDone: () -> Unit
): BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED->{
                onScanDone()
            }
        }
    }

}