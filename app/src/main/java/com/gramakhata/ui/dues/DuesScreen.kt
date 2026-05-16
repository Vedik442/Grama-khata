package com.gramakhata.ui.dues

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.gramakhata.data.Customer
import com.gramakhata.data.CustomerViewModel
import com.gramakhata.data.balance
import com.gramakhata.theme.*
import com.gramakhata.ui.Screen
import com.gramakhata.ui.components.BottomNavBar
import com.gramakhata.ui.components.BalanceChip
import com.gramakhata.ui.components.HeroHeader

@Composable
fun DuesScreen(viewModel: CustomerViewModel, navController: NavController) {
    val customers by viewModel.customers.collectAsState()

    val dues = remember(customers) {
        customers.map { it to it.balance() }
            .filter { (_, b) -> b > 0 }
            .sortedByDescending { (_, b) -> b }
    }
    val total = dues.sumOf { (_, b) -> b }

    Scaffold(
        topBar = {
            HeroHeader(title = "Dues Dashboard")
        },
        bottomBar = { BottomNavBar(navController) },
        containerColor = CreamBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Total pending banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(TealPrimary, Color(0xFF2A9EC0), Color(0xFF5A60C8))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            "TOTAL PENDING",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "₹${"%.0f".format(total)}",
                            fontSize   = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                "${dues.size} customer${if (dues.size == 1) "" else "s"} pending",
                                color    = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style    = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            if (dues.isEmpty()) {
                item {
                    Text(
                        "All settled! 🎉",
                        color    = MutedForeground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            } else {
                items(dues) { (customer, bal) ->
                    DueRow(
                        customer = customer,
                        balance  = bal,
                        onClick  = {
                            navController.navigate(Screen.CustomerDetail.createRoute(customer.id))
                        }
                    )
                }
            }
        }
    }
}

private fun severityColors(amount: Double): Triple<Color, Color, String> {
    return when {
        amount >= 1000 -> Triple(CoralDanger.copy(alpha = 0.12f),   CoralDanger,    "High")
        amount >= 300  -> Triple(AmberWarning.copy(alpha = 0.12f),  AmberWarning,   "Medium")
        else           -> Triple(EmeraldSuccess.copy(alpha = 0.12f),EmeraldSuccess, "Low")
    }
}

@Composable
private fun DueRow(customer: Customer, balance: Double, onClick: () -> Unit) {
    val (bg, textColor, label) = severityColors(balance)

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bg)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(customer.phone, style = MaterialTheme.typography.bodyMedium, color = MutedForeground)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${"%.0f".format(balance)}",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Surface(
                    color = textColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        label,
                        color    = textColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
