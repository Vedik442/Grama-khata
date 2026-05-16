package com.gramakhata.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.gramakhata.data.CustomerViewModel
import com.gramakhata.data.TransactionType
import com.gramakhata.theme.*
import com.gramakhata.ui.components.BottomNavBar
import com.gramakhata.ui.components.HeroHeader
import java.time.LocalDate

@Composable
fun ReportsScreen(viewModel: CustomerViewModel, navController: NavController) {
    val customers by viewModel.customers.collectAsState()
    val today = LocalDate.now().toString()

    var creditToday   = 0.0
    var collectedToday = 0.0
    var creditAll     = 0.0
    var collectedAll  = 0.0

    for (c in customers) {
        for (t in c.transactions) {
            if (t.type == TransactionType.CREDIT) {
                creditAll += t.amount
                if (t.date == today) creditToday += t.amount
            } else {
                collectedAll += t.amount
                if (t.date == today) collectedToday += t.amount
            }
        }
    }
    val netToday = creditToday - collectedToday

    Scaffold(
        topBar = { HeroHeader(title = "Daily Report", subtitle = "Today's activity at a glance") },
        bottomBar = { BottomNavBar(navController) },
        containerColor = CreamBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Net Today banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Brush.linearGradient(listOf(AmberWarning, CoralDanger)))
                        .padding(22.dp)
                ) {
                    Column {
                        Text("NET TODAY", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.88f))
                        Text(
                            "${if (netToday >= 0) "+" else "−"}₹${"%.0f".format(kotlin.math.abs(netToday))}",
                            fontSize   = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White
                        )
                        Text("Credit minus collections", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }

            item {
                Text(
                    "TODAY",
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MutedForeground,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
            item { StatCard(icon = Icons.Default.TrendingUp,   label = "Credit Given",     value = creditToday,    toneColor = CoralDanger) }
            item { StatCard(icon = Icons.Default.TrendingDown,  label = "Total Collected",  value = collectedToday, toneColor = EmeraldSuccess) }
            item { StatCard(icon = Icons.Default.Wallet,        label = "Net Balance",       value = netToday,       toneColor = TealPrimary) }

            item {
                Text(
                    "ALL TIME",
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MutedForeground,
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp)
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AllTimeCard("CREDIT", creditAll,    Brush.linearGradient(listOf(CoralDanger, Color(0xFFD4762A))),    Modifier.weight(1f))
                    AllTimeCard("COLLECTED", collectedAll, Brush.linearGradient(listOf(EmeraldSuccess, TealPrimary)), Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatCard(icon: ImageVector, label: String, value: Double, toneColor: Color) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(toneColor.copy(alpha = 0.12f)),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = toneColor, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MutedForeground)
                Text("₹${"%.0f".format(kotlin.math.abs(value))}", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun AllTimeCard(label: String, value: Double, gradient: Brush, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .padding(18.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.88f))
            Spacer(Modifier.height(4.dp))
            Text("₹${"%.0f".format(value)}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}
