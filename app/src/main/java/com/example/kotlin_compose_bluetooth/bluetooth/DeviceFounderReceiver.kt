package com.example.kotlin_compose_bluetooth.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DeviceFounderReceiver(
    private val onDeviceFound: (BluetoothDevice) -> Unit
): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        when(intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                //val deviceName = device?.name
                //val deviceHardwareAddress = device?.address // MAC address
                //println(deviceName)
                //println(deviceHardwareAddress)
                device?.let(onDeviceFound)
            }
        }

    }

}