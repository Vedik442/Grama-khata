package com.gramakhata.ui.customer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gramakhata.data.CustomerViewModel
import com.gramakhata.theme.*
import com.gramakhata.ui.components.AppTopBar

@Composable
fun NewCustomerScreen(
    viewModel: CustomerViewModel,
    onBack: () -> Unit,
    onCreated: (String) -> Unit
) {
    var name     by remember { mutableStateOf("") }
    var phone    by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) photoUri = uri
    }

    Scaffold(
        topBar = { AppTopBar(title = "Add Customer", onBack = onBack) },
        containerColor = CreamBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo picker
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(PrimaryContainer)
                    .clickable { photoLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(36.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("Tap to add photo", style = MaterialTheme.typography.bodyMedium, color = MutedForeground)
            Spacer(Modifier.height(28.dp))

            OutlinedTextField(
                value         = name,
                onValueChange = { name = it },
                label         = { Text("Name") },
                placeholder   = { Text("e.g. Ramesh Kumar") },
                singleLine    = true,
                shape         = RoundedCornerShape(14.dp),
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = SoftBorder,
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite
                )
            )
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value         = phone,
                onValueChange = { phone = it },
                label         = { Text("Phone Number") },
                placeholder   = { Text("10-digit mobile") },
                singleLine    = true,
                shape         = RoundedCornerShape(14.dp),
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = SoftBorder,
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite
                )
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.addCustomer(
                        name     = name.trim(),
                        phone    = phone.trim(),
                        photoUri = photoUri?.toString(),
                        onComplete = { id -> onCreated(id) }
                    )
                },
                enabled  = name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Save Customer", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
