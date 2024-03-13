package com.example.kotlin_compose_bluetooth.screen

enum class Screen {
    PERMISSION,
    CONNECTION,
    MESSAGE
}

sealed class NavItem(val route: String){
    object Permission : NavItem(Screen.PERMISSION.name)
    object Scan : NavItem(Screen.CONNECTION.name)
    object Message: NavItem(Screen.MESSAGE.name)
}