package com.example.kotlin_compose_bluetooth.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_compose_bluetooth.CLIENT_NAME
import com.example.kotlin_compose_bluetooth.model.BluetoothDeviceModel
import com.example.kotlin_compose_bluetooth.testDeviceModelList
import com.example.kotlin_compose_bluetooth.ui.theme.KotlinComposeBluetoothTheme

@Composable
fun ScanScreen(
    pairedDevicesList: List<BluetoothDeviceModel>,
    isBluetoothEnabled: Boolean = false,
    isConnecting: Boolean = true,
    connectionResult: Boolean = true,
    onNextButtonClicked: ()->Unit = {},
    onConnectButtonClicked: (device: BluetoothDeviceModel) -> Unit = {},
    onBluetoothOnButtonClicked: () -> Unit = {}
){
    Column(modifier = Modifier.fillMaxSize()) {

        Text(text = "Connect to HC-05", fontSize = 30.sp)
        Column(
            modifier = Modifier
                .padding(10.dp),
        ) {

            if(isBluetoothEnabled){
                BluetoothDevicesList(
                    modifier = Modifier,
                    pairedDevicesList = pairedDevicesList,
                    onBluetoothOnButtonClicked = onBluetoothOnButtonClicked
                ){
                    onConnectButtonClicked(it)
                }
            }else{
                Text(text = "Bluetooth is currently off")
                Button(onClick = onBluetoothOnButtonClicked) {
                    Text(text = "Turn bluetooth on")
                }
            }

        }

        Text(
            color = if(connectionResult && isBluetoothEnabled) Color.Green else Color.Red,
            text = "Connection result: ${if(connectionResult && isBluetoothEnabled) "connected to HC05" else "not connected to HC05"}"
        )

        Spacer(modifier = Modifier.padding(50.dp))

        ConnectingBar(isConnecting = isConnecting)

        Spacer(modifier = Modifier.padding(50.dp))

        if (connectionResult && isBluetoothEnabled){
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 40.dp,
                        end = 40.dp
                    ),
                onClick = onNextButtonClicked,
                shape = RectangleShape
            ) {
                Text(text = "Go to the terminal")
                Spacer(modifier = Modifier.padding(10.dp))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Go to message screen")
            }
        }
    }

}

@Composable
fun BluetoothDevicesList(
    modifier: Modifier,
    pairedDevicesList: List<BluetoothDeviceModel> = testDeviceModelList,
    onBluetoothOnButtonClicked: () -> Unit = {},
    onConfirm: (BluetoothDeviceModel) -> Unit,
){
    var selectedItemIndex by remember { mutableStateOf(-1) }
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        item {
            Text(text = "Paired Devices", fontSize = 20.sp)
        }

        itemsIndexed(pairedDevicesList){index, device ->
            device.name?.let {
                Text(
                    text = it,
                    color = if (it == CLIENT_NAME) Color.Green else Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedItemIndex = index
                            showDialog = true
                        }
                )
            }
        }
    }



    if (showDialog){
        AlertDialog(
            title = {
                Text(text = "Connect Request")
            },
            text = {
                Text(text = "Are you sure to connect ${pairedDevicesList[selectedItemIndex]}")
            },
            onDismissRequest = {

                showDialog = false
                selectedItemIndex = -1
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(pairedDevicesList[selectedItemIndex])
                    showDialog = false
                    selectedItemIndex = -1
                }) {
                    Text(text = "Connect")
                }
            },
            dismissButton ={
                TextButton(
                    onClick = {
                        showDialog = false
                        selectedItemIndex = -1
                    }
                ) {
                    Text("Dismiss")
                }
            },
        )
    }

}

@Composable
fun ConnectingBar(isConnecting: Boolean){

    if (isConnecting){
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            CircularProgressIndicator(
                modifier = Modifier.width(32.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(text = "Connecting to device")
        }
    }

}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScanScreenPreview() {
    KotlinComposeBluetoothTheme {
        ScanScreen(
            isBluetoothEnabled = true,
            pairedDevicesList = testDeviceModelList
        )
    }
}