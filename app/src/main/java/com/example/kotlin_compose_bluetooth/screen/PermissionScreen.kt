package com.example.kotlin_compose_bluetooth.screen

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlin_compose_bluetooth.ui.theme.KotlinComposeBluetoothTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(
    multiplePermission: MultiplePermissionsState,
    onPermissionGranted: () -> Unit = {}
) {
    //multiplePermission.allPermissionsGranted
    if (multiplePermission.allPermissionsGranted) {
        onPermissionGranted()
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                getTextToShowGivenPermissions(
                    multiplePermission.revokedPermissions,
                    multiplePermission.shouldShowRationale
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { multiplePermission.launchMultiplePermissionRequest() }) {
                Text("Request permissions")
            }
        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
private fun getTextToShowGivenPermissions(
    permissions: List<PermissionState>,
    shouldShowRationale: Boolean
): String {

    val revokedPermissionsSize = permissions.size
    if (revokedPermissionsSize == 0) return ""

    val text = StringBuilder().apply {
        append("The ")
    }

    for (i in permissions.indices) {
        text.append(permissions[i].permission)
        when {
            revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                text.append(", and ")
            }
            i == revokedPermissionsSize - 1 -> {
                text.append(" ")
            }
            else -> {
                text.append(", ")
            }
        }
    }
    text.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
    text.append(
        if (shouldShowRationale) {
            " important. To function app properly, please grant permissions."

        } else {
            " denied. Please grant permissions to continue."
        }
    )
    return text.toString()
    
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PermissionScreenPreview() {
    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions_API_31_HIGHER
    )
    KotlinComposeBluetoothTheme {
        PermissionScreen(multiplePermission = multiplePermissionsState)
    }
}


val permissions_API_31_HIGHER = listOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.BLUETOOTH_SCAN,
)

val permissions_API_30_LOWER = listOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)