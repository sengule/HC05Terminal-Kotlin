package com.example.kotlin_compose_bluetooth.ui.theme.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_compose_bluetooth.viewmodels.BluetoothViewModel
import com.example.kotlin_compose_bluetooth.bluetooth.AppBluetoothController
import com.example.kotlin_compose_bluetooth.bluetooth.ConnectionResult
import com.example.kotlin_compose_bluetooth.model.BluetoothDeviceModel
import com.example.kotlin_compose_bluetooth.testDeviceModelList
import com.example.kotlin_compose_bluetooth.ui.theme.KotlinComposeBluetoothTheme

@Composable
fun ScanScreen(
    //bluetoothViewModel: BluetoothViewModel = viewModel(factory = BluetoothViewModel.provideFactory(AppBluetoothController(LocalContext.current))),
    scannedDevicesList: List<BluetoothDeviceModel> = testDeviceModelList,
    pairedDevicesList: List<BluetoothDeviceModel> = testDeviceModelList,
    isScanning: Boolean = true,
    connectionResult: Boolean = true,
    onNextButtonClicked: ()->Unit = {},
    onScanButtonClicked: () -> Unit = {},
    onStopButtonClicked: () -> Unit = {},
    onConnectButtonClicked: (device: BluetoothDeviceModel) -> Unit = {}
){
    //val connectionState = bluetoothViewModel.connectionState.collectAsState().value
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = "Connect to HC-05", modifier = Modifier.weight(1f), fontSize = 30.sp)

            if (connectionResult){
                IconButton(onClick = onNextButtonClicked) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Go to message screen")
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
        ) {

            BluetoothDevicesList(
                modifier = Modifier.weight(1.0f),
                scannedDevicesList = scannedDevicesList,//bluetoothViewModel.state.collectAsState().value.scannedDevices,
                pairedDevicesList = pairedDevicesList//bluetoothViewModel.state.collectAsState().value.pairedDevices
            ){
                //Show are you sure to pair that device?
                //bluetoothViewModel.connect(it)
                onConnectButtonClicked(it)
            }
            /* */

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Button(
                    onClick = onScanButtonClicked
                ) {
                    Text(text = "Scan")
                }

                ScanningBar(isScanning = isScanning)

                Button(
                    onClick = onStopButtonClicked
                ) {
                    Text(text = "Stop")
                }
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PairRequestDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
){

    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Pair")
            }
        },
        dismissButton ={
            TextButton(
                onClick = onDismiss
            ) {
                Text("Dismiss")
            }
        },
    )

}


@Composable
fun BluetoothDevicesList(
    modifier: Modifier,
    scannedDevicesList: List<BluetoothDeviceModel> = testDeviceModelList,
    pairedDevicesList: List<BluetoothDeviceModel> = testDeviceModelList,
    onConfirm: (BluetoothDeviceModel) -> Unit,
){
    var selectedItemIndex by remember { mutableStateOf(-1) }
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        item {
            Text(text = "Scanned Devices", fontSize = 20.sp)
        }

        itemsIndexed(scannedDevicesList){index, device ->
            device.name?.let {
                Text(
                    text = it,
                    modifier = Modifier.clickable {
                        selectedItemIndex = index
                        showDialog = true
                    }
                )
            }
        }

    }

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
                    modifier = Modifier.clickable {
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
                Text(text = "Pair Request")
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
                    Text(text = "Pair")
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
fun ScanningBar(isScanning: Boolean){

    if (isScanning){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            CircularProgressIndicator(
                modifier = Modifier.width(32.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(text = "Scanning")
        }
    }

}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScanScreenPreview() {
    KotlinComposeBluetoothTheme {
        ScanScreen()
    }
}