package com.example.kotlin_compose_bluetooth.bluetooth

import android.bluetooth.BluetoothSocket
import com.example.kotlin_compose_bluetooth.CLIENT_NAME
import com.example.kotlin_compose_bluetooth.model.MessageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothMessageService(private val mSocket: BluetoothSocket) {

    private val mInputStream = mSocket.inputStream
    private val mOutputSteam = mSocket.outputStream
    private val mmBuffer: ByteArray = ByteArray(1024)

    suspend fun sendMessage(bytes: ByteArray): Boolean{
        return withContext(Dispatchers.IO){
            try {
                mOutputSteam?.write(bytes)
                true
            }catch (e: IOException){
                println("Error When writing message ${e.message}")
                return@withContext false
            }
        }
    }

    fun listenModuleMessages(): Flow<MessageModel>{
        return flow {

            if (!mSocket.isConnected)
                return@flow

            var message = ""

            while (true){
                val numBytes = try {
                    mInputStream.read(mmBuffer)
                }catch (e: IOException){
                    println("There is an error while reading ${e.message}")
                    break
                }

                message += mmBuffer.decodeToString(endIndex = numBytes)

                if (message.endsWith("\n")){
                    val messageModel = message.toMessageModel(CLIENT_NAME)
                    emit(messageModel)
                    message = ""
                }

            }

        }.flowOn(Dispatchers.IO)
    }

}

fun String.toMessageModel(sender: String): MessageModel{
    return MessageModel(
        sender = sender,
        message = this
    )
}