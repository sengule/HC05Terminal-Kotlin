package com.example.kotlin_compose_bluetooth.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kotlin_compose_bluetooth.bluetooth.AppBluetoothController
import com.example.kotlin_compose_bluetooth.screen.NavItem
import com.example.kotlin_compose_bluetooth.screen.Screen
import com.example.kotlin_compose_bluetooth.testMessages
import com.example.kotlin_compose_bluetooth.ui.theme.screens.MessageScreen
import com.example.kotlin_compose_bluetooth.ui.theme.screens.PermissionScreen
import com.example.kotlin_compose_bluetooth.ui.theme.screens.ScanScreen
import com.example.kotlin_compose_bluetooth.viewmodels.BluetoothViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavItem.Permission.route,
    permissions: MultiplePermissionsState
){

    val bluetoothViewModel: BluetoothViewModel = viewModel(factory = BluetoothViewModel.provideFactory(AppBluetoothController(LocalContext.current)))

    NavHost(navController = navController, startDestination = startDestination){

        composable(NavItem.Permission.route){
            PermissionScreen(multiplePermission = permissions)
        }

        composable(NavItem.Scan.route){
            ScanScreen(
                scannedDevicesList = bluetoothViewModel.state.collectAsState().value.scannedDevices,
                pairedDevicesList = bluetoothViewModel.state.collectAsState().value.pairedDevices,
                isScanning = bluetoothViewModel.isScanning.collectAsState().value,
                connectionResult = bluetoothViewModel.state.collectAsState().value.isConnected,
                onNextButtonClicked = {navController.navigate(Screen.MESSAGE.name)},
                onScanButtonClicked = {bluetoothViewModel.startScan()},
                onStopButtonClicked = {bluetoothViewModel.stopScan()},
                onConnectButtonClicked = { device-> bluetoothViewModel.connect(device)}
            )
        }

        composable(NavItem.Message.route){
            MessageScreen(
                bluetoothViewModel.state.collectAsState().value.messages,
                onBackButtonClicked = {navController.navigate(Screen.SCAN.name)},
                onSendMessage = {message -> bluetoothViewModel.sendMessage(message)}
            )
        }

    }
}