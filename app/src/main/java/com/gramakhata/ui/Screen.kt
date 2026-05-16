package com.gramakhata.ui

sealed class Screen(val route: String) {
    object Home         : Screen("home")
    object Dues         : Screen("dues")
    object Reports      : Screen("reports")
    object NewCustomer  : Screen("customer/new")
    object CustomerDetail : Screen("customer/{id}") {
        fun createRoute(id: String) = "customer/$id"
    }
}
