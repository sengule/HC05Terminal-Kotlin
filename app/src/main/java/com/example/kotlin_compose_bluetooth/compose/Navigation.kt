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
import com.example.kotlin_compose_bluetooth.screen.MessageScreen
import com.example.kotlin_compose_bluetooth.screen.PermissionScreen
import com.example.kotlin_compose_bluetooth.screen.ScanScreen
import com.example.kotlin_compose_bluetooth.vm.BluetoothViewModel
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
            PermissionScreen(
                onPermissionGranted = {
                    bluetoothViewModel.updatePairedDevices()
                    navController.navigate(Screen.CONNECTION.name)
                                      },
                multiplePermission = permissions
            )
        }

        composable(NavItem.Scan.route){
            ScanScreen(
                pairedDevicesList = bluetoothViewModel.state.collectAsState().value.pairedDevices,
                isBluetoothEnabled = bluetoothViewModel.state.collectAsState().value.isEnabled,
                isConnecting = bluetoothViewModel.isConnecting.collectAsState().value,
                connectionResult = bluetoothViewModel.state.collectAsState().value.isConnected,
                onNextButtonClicked = {navController.navigate(Screen.MESSAGE.name)},
                onConnectButtonClicked = { device-> bluetoothViewModel.connect(device)},
                onBluetoothOnButtonClicked = {bluetoothViewModel.bluetoothEnableRequest()}
            )
        }

        composable(NavItem.Message.route){
            MessageScreen(
                bluetoothViewModel.state.collectAsState().value.messages,
                onBackButtonClicked = {navController.navigate(Screen.CONNECTION.name)},
                onSendMessage = {message -> bluetoothViewModel.sendMessage(message)}
            )
        }

    }
}