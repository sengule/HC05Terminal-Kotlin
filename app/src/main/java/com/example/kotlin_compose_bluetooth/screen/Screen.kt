package com.example.kotlin_compose_bluetooth.screen

enum class Screen {
    PERMISSION,
    SCAN,
    MESSAGE
}

sealed class NavItem(val route: String){
    object Permission : NavItem(Screen.PERMISSION.name)
    object Scan : NavItem(Screen.SCAN.name)
    object Message: NavItem(Screen.MESSAGE.name)
}