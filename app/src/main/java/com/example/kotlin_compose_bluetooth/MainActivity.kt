package com.example.kotlin_compose_bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.navigation.compose.rememberNavController
import com.example.kotlin_compose_bluetooth.compose.AppNavHost
import com.example.kotlin_compose_bluetooth.screen.NavItem
import com.example.kotlin_compose_bluetooth.ui.theme.KotlinComposeBluetoothTheme
import com.example.kotlin_compose_bluetooth.ui.theme.screens.permissions_API_31_HIGHER
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //UI
        setContent {
            KotlinComposeBluetoothTheme {
                val multiplePermissionsState = rememberMultiplePermissionsState(
                    permissions_API_31_HIGHER
                )
                AppNavHost(
                    navController = rememberNavController(),
                    permissions = multiplePermissionsState,
                    startDestination = if (multiplePermissionsState.allPermissionsGranted) NavItem.Scan.route else NavItem.Permission.route
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}


