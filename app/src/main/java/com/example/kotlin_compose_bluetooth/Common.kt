package com.example.kotlin_compose_bluetooth

import android.content.Context
import android.content.pm.PackageManager
import android.os.Message
import com.example.kotlin_compose_bluetooth.model.BluetoothDeviceModel
import com.example.kotlin_compose_bluetooth.model.MessageModel

fun hasPermission(context: Context, permission: String): Boolean{
    return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

val SERVER_NAME = "Android-Server"
val CLIENT_NAME = "HC-05"


val testDeviceModelList = arrayListOf<BluetoothDeviceModel>(
    BluetoothDeviceModel("Hello", "123"),
    BluetoothDeviceModel("Hello", "123"),
    BluetoothDeviceModel("Hello", "123"),
    BluetoothDeviceModel("Hello", "123"),
    BluetoothDeviceModel("Hello", "123"),
    BluetoothDeviceModel("Hello", "123"),
    BluetoothDeviceModel("HC-05", "123"),
)

val testMessages = arrayListOf<MessageModel>(
    MessageModel("HC-05", "Hello"),
    MessageModel(SERVER_NAME, "Hello"),
    MessageModel(SERVER_NAME, "How Are you"),
    MessageModel("HC-05", "fine"),
    MessageModel("HC-05", "Bye"),
    MessageModel(SERVER_NAME, "Bye"),
)