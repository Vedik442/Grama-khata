package com.gramakhata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gramakhata.data.Customer
import com.gramakhata.data.CustomerViewModel
import com.gramakhata.theme.GramaKhataTheme
import com.gramakhata.ui.Screen
import com.gramakhata.ui.customer.CustomerDetailScreen
import com.gramakhata.ui.customer.NewCustomerScreen
import com.gramakhata.ui.dues.DuesScreen
import com.gramakhata.ui.home.HomeScreen
import com.gramakhata.ui.reports.ReportsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GramaKhataTheme {
                GramaKhataApp()
            }
        }
    }
}

@Composable
fun GramaKhataApp() {
    val navController   = rememberNavController()
    val viewModel: CustomerViewModel = viewModel()

    // Bottom-sheet customer selection state (for home-screen tap)
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }

    NavHost(
        navController    = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel       = viewModel,
                navController   = navController,
                onCustomerClick = { customer ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customer.id))
                }
            )
        }

        composable(Screen.Dues.route) {
            DuesScreen(viewModel = viewModel, navController = navController)
        }

        composable(Screen.Reports.route) {
            ReportsScreen(viewModel = viewModel, navController = navController)
        }

        composable(Screen.NewCustomer.route) {
            NewCustomerScreen(
                viewModel  = viewModel,
                onBack     = { navController.popBackStack() },
                onCreated  = { id ->
                    navController.navigate(Screen.CustomerDetail.createRoute(id)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.CustomerDetail.route) { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            CustomerDetailScreen(
                customerId = id,
                viewModel  = viewModel,
                onBack     = { navController.popBackStack() }
            )
        }
    }
}
