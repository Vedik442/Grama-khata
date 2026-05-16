package com.gramakhata.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.collectAsState
import com.gramakhata.data.*
import com.gramakhata.theme.*
import com.gramakhata.ui.components.*
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: String,
    viewModel: CustomerViewModel,
    onBack: () -> Unit
) {
    val customers by viewModel.customers.collectAsState()
    val customer  = customers.find { it.id == customerId }
    val context   = LocalContext.current

    if (customer == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TealPrimary)
        }
        return
    }

    val bal = customer.balance()
    var showTxnSheet  by remember { mutableStateOf(false) }
    var txnMode       by remember { mutableStateOf(TransactionType.CREDIT) }
    var showReminder  by remember { mutableStateOf(false) }

    val reminderMsg = "Namaste ${customer.name}, aapka ₹${"%.0f".format(bal)} udhaar baki hai. " +
            "Kripya jaldi se chukta karein. Dhanyavaad. — Grama-Khata"

    Scaffold(
        topBar = {
            AppTopBar(title = customer.name, onBack = onBack)
        },
        containerColor = CreamBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Profile card
            item {
                ProfileCard(customer = customer, balance = bal)
            }

            // Action buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            txnMode = TransactionType.CREDIT
                            showTxnSheet = true
                        },
                        modifier = Modifier.weight(1f),
                        colors   = ButtonDefaults.buttonColors(containerColor = CoralDanger),
                        shape    = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Give Credit", fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = {
                            txnMode = TransactionType.PAYMENT
                            showTxnSheet = true
                        },
                        modifier = Modifier.weight(1f),
                        colors   = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape    = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Receive", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Send Reminder
            if (bal > 0) {
                item {
                    OutlinedButton(
                        onClick = { showReminder = !showReminder },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        )
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Send Reminder", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 6.dp))
                    }
                }
            }

            // Reminder preview
            if (showReminder && bal > 0) {
                item {
                    Card(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "MESSAGE PREVIEW",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedForeground
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(reminderMsg, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        val encoded = URLEncoder.encode(reminderMsg, "UTF-8")
                                        val uri = Uri.parse("https://wa.me/${customer.phone.filter { it.isDigit() }}?text=$encoded")
                                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors   = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                    shape    = RoundedCornerShape(10.dp)
                                ) { Text("WhatsApp", fontWeight = FontWeight.SemiBold) }
                                Button(
                                    onClick = {
                                        val uri = Uri.parse("sms:${customer.phone}")
                                        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                                            putExtra("sms_body", reminderMsg)
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors   = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape    = RoundedCornerShape(10.dp)
                                ) { Text("SMS", fontWeight = FontWeight.SemiBold) }
                            }
                        }
                    }
                }
            }

            // Transaction history header
            item {
                Text(
                    "HISTORY",
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MutedForeground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            val history = customer.transactions.reversed()
            if (history.isEmpty()) {
                item {
                    Text(
                        "No transactions yet",
                        color    = MutedForeground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            } else {
                items(history) { txn ->
                    TransactionRow(txn = txn)
                }
            }
        }
    }

    if (showTxnSheet) {
        TransactionSheet(
            mode         = txnMode,
            customerName = customer.name,
            onDismiss    = { showTxnSheet = false },
            onSave       = { amount, type, note ->
                viewModel.addTransaction(customer.id, amount, type, note)
                showTxnSheet = false
            }
        )
    }
}

@Composable
private fun ProfileCard(customer: Customer, balance: Double) {
    val amountColor = when {
        balance > 0  -> CoralDanger
        balance < 0  -> EmeraldSuccess
        else         -> MutedForeground
    }
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier         = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(TealPrimary, TealLight))),
                contentAlignment = Alignment.Center
            ) {
                Text(initialsOf(customer.name), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Text(customer.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(customer.phone, style = MaterialTheme.typography.bodyMedium, color = MutedForeground)
            Spacer(Modifier.height(10.dp))
            Text(
                text       = "₹${"%.0f".format(kotlin.math.abs(balance))}",
                fontSize   = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = amountColor
            )
            Text(
                text  = when { balance > 0 -> "Total Due" ; balance < 0 -> "Advance" ; else -> "Settled" },
                style = MaterialTheme.typography.labelSmall,
                color = MutedForeground
            )
        }
    }
}

@Composable
private fun TransactionRow(txn: Transaction) {
    val isCredit = txn.type == TransactionType.CREDIT
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text       = if (isCredit) "Credit Given" else "Payment Received",
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text  = txn.date + if (!txn.note.isNullOrBlank()) " • ${txn.note}" else "",
                style = MaterialTheme.typography.labelSmall,
                color = MutedForeground
            )
        }
        Text(
            text       = "${if (isCredit) "+" else "−"}₹${"%.0f".format(txn.amount)}",
            fontWeight = FontWeight.ExtraBold,
            color      = if (isCredit) CoralDanger else EmeraldSuccess,
            fontSize   = 16.sp
        )
    }
    Spacer(Modifier.height(2.dp))
}

// ─── Transaction Bottom Sheet ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSheet(
    mode: TransactionType,
    customerName: String,
    onDismiss: () -> Unit,
    onSave: (Double, TransactionType, String?) -> Unit
) {
    var type   by remember { mutableStateOf(mode) }
    var amount by remember { mutableStateOf("") }
    var note   by remember { mutableStateOf("") }

    val scope     = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = sheetState,
        containerColor    = CardWhite,
        shape             = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("Add Transaction", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("For $customerName", style = MaterialTheme.typography.bodyMedium, color = MutedForeground)
            Spacer(Modifier.height(16.dp))

            // Credit / Payment toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(TransactionType.CREDIT to "+ Credit", TransactionType.PAYMENT to "− Payment").forEach { (t, label) ->
                    val selected = type == t
                    val bgColor  = if (selected) (if (t == TransactionType.CREDIT) CoralDanger else EmeraldSuccess) else Color.Transparent
                    Button(
                        onClick = { type = t },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = bgColor,
                            contentColor   = if (selected) Color.White else MutedForeground
                        ),
                        shape     = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(label, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value         = amount,
                onValueChange = { amount = it },
                placeholder   = { Text("₹ Amount", fontSize = 22.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine    = true,
                textStyle     = LocalTextStyle.current.copy(fontSize = 26.sp, fontWeight = FontWeight.Bold),
                shape         = RoundedCornerShape(14.dp),
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = TealPrimary,
                    unfocusedBorderColor = SoftBorder
                )
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value         = note,
                onValueChange = { note = it },
                placeholder   = { Text("Note (optional)") },
                singleLine    = true,
                shape         = RoundedCornerShape(14.dp),
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = TealPrimary,
                    unfocusedBorderColor = SoftBorder
                )
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick  = {
                    val n = amount.toDoubleOrNull()
                    if (n != null && n > 0) onSave(n, type, note.trim().ifBlank { null })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape    = RoundedCornerShape(14.dp),
                enabled  = amount.toDoubleOrNull()?.let { it > 0 } ?: false,
                colors   = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Save", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
