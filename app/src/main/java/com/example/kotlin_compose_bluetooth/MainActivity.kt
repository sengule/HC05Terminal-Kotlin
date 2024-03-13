package com.example.kotlin_compose_bluetooth

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.navigation.compose.rememberNavController
import com.example.kotlin_compose_bluetooth.compose.AppNavHost
import com.example.kotlin_compose_bluetooth.screen.NavItem
import com.example.kotlin_compose_bluetooth.ui.theme.KotlinComposeBluetoothTheme
import com.example.kotlin_compose_bluetooth.screen.permissions_API_30_LOWER
import com.example.kotlin_compose_bluetooth.screen.permissions_API_31_HIGHER
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //UI
        setContent {
            KotlinComposeBluetoothTheme {
                val multiplePermissionsState = rememberMultiplePermissionsState(
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        permissions_API_31_HIGHER
                    else
                        permissions_API_30_LOWER
                )
                AppNavHost(
                    navController = rememberNavController(),
                    permissions = multiplePermissionsState,
                    startDestination = getStartScreen(multiplePermissionsState)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun getStartScreen(permissionState: MultiplePermissionsState): String{
        return if (permissionState.allPermissionsGranted)
            NavItem.Scan.route
        else
            NavItem.Permission.route
    }

}


