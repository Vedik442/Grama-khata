package com.gramakhata.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import com.gramakhata.data.Customer
import com.gramakhata.data.CustomerViewModel
import com.gramakhata.data.balance
import com.gramakhata.theme.*
import com.gramakhata.ui.Screen
import com.gramakhata.ui.components.*
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    viewModel: CustomerViewModel,
    navController: NavController,
    onCustomerClick: (Customer) -> Unit
) {
    val customers by viewModel.customers.collectAsState()
    var query by remember { mutableStateOf("") }

    val filtered = remember(customers, query) {
        customers
            .filter {
                it.name.contains(query, ignoreCase = true) || it.phone.contains(query)
            }
            .sortedByDescending { it.balance() }
    }

    val totalDue = remember(customers) {
        customers.sumOf { b -> if (b.balance() > 0) b.balance() else 0.0 }
    }
    val totalAdvance = remember(customers) {
        customers.sumOf { b -> if (b.balance() < 0) -b.balance() else 0.0 }
    }

    Scaffold(
        topBar = {
            HeroHeader(title = "Grama-Khata", subtitle = "Your digital udhaar book")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.NewCustomer.route) },
                containerColor = TealPrimary,
                contentColor   = Color.White,
                shape          = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer", modifier = Modifier.size(28.dp))
            }
        },
        bottomBar = { BottomNavBar(navController) },
        containerColor = CreamBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Summary cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-20).dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        label   = "TO COLLECT",
                        amount  = totalDue,
                        gradient = Brush.linearGradient(listOf(Color(0xFFD9503A), Color(0xFFD4762A))),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label    = "ADVANCE",
                        amount   = totalAdvance,
                        gradient = Brush.linearGradient(listOf(Color(0xFF3DA876), Color(0xFF1A8C7A))),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value         = query,
                    onValueChange = { query = it },
                    placeholder   = { Text("Search customers") },
                    leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(18.dp),
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = TealPrimary,
                        unfocusedBorderColor = SoftBorder,
                        focusedContainerColor   = CardWhite,
                        unfocusedContainerColor = CardWhite
                    )
                )
            }

            // Customers count label
            item {
                Text(
                    text     = "CUSTOMERS · ${filtered.size}",
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MutedForeground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            if (filtered.isEmpty()) {
                item { EmptyState(hasQuery = query.isNotEmpty()) }
            } else {
                itemsIndexed(filtered) { index, customer ->
                    CustomerRow(
                        customer  = customer,
                        tintIndex = index,
                        onClick   = { onCustomerClick(customer) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    amount: Double,
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = "₹%.0f".format(amount),
                style      = MaterialTheme.typography.headlineMedium,
                color      = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun CustomerRow(customer: Customer, tintIndex: Int, onClick: () -> Unit) {
    val bal     = customer.balance()
    val gradients = avatarGradients
    val gradient  = gradients[tintIndex % gradients.size]
    val amountColor = when {
        bal > 0  -> CoralDanger
        bal < 0  -> EmeraldSuccess
        else     -> MutedForeground
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = initialsOf(customer.name),
                    color      = Color.White,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = customer.name,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = MutedForeground, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(text = customer.phone, style = MaterialTheme.typography.bodyMedium, color = MutedForeground)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text       = "₹%.0f".format(kotlin.math.abs(bal)),
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = amountColor
                )
                BalanceChip(amount = bal)
            }
        }
    }
}

@Composable
private fun EmptyState(hasQuery: Boolean) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier         = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(TealPrimary, TealLight))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text  = if (hasQuery) "No matches found" else "No customers yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text  = if (hasQuery) "Try a different name or phone number."
                    else "Tap the + button to add your first customer.",
            style  = MaterialTheme.typography.bodyMedium,
            color  = MutedForeground,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}
