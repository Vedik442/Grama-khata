package com.gramakhata.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gramakhata.theme.*
import com.gramakhata.ui.Screen

// ─── Gradient Hero Header ────────────────────────────────────────────────────

@Composable
fun HeroHeader(
    title: String,
    subtitle: String? = null,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    val heroGradient = Brush.linearGradient(
        colors = listOf(TealPrimary, Color(0xFF2A9EC0), Color(0xFF5A60C8))
    )
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(heroGradient)
            .padding(top = statusBarHeight + 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
    ) {
        // Decorative blobs
        Box(
            Modifier
                .size(160.dp)
                .offset(x = 200.dp, y = (-40).dp)
                .background(Color.White.copy(alpha = 0.12f), CircleShape)
        )
        Box(
            Modifier
                .size(140.dp)
                .offset(x = (-30).dp, y = 60.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📖", fontSize = 22.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
            if (actions != null) {
                Row(content = actions)
            }
        }
    }
}

// ─── Simple Sticky Header ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = { actions?.invoke(this) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

// ─── Bottom Navigation Bar ────────────────────────────────────────────────────

data class NavItem(val label: String, val icon: ImageVector, val screen: Screen)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem("Home",    Icons.Default.Home,     Screen.Home),
        NavItem("Dues",    Icons.Default.Warning,  Screen.Dues),
        NavItem("Reports", Icons.Default.BarChart,  Screen.Reports),
    )
    val backStack = navController.currentBackStackEntryAsState()
    val current   = backStack.value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = current == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick  = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon  = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor        = Color.White,
                    selectedTextColor        = TealPrimary,
                    indicatorColor           = TealPrimary,
                    unselectedIconColor      = MutedForeground,
                    unselectedTextColor      = MutedForeground
                )
            )
        }
    }
}

// ─── Currency Chip ────────────────────────────────────────────────────────────

@Composable
fun BalanceChip(amount: Double, modifier: Modifier = Modifier) {
    val (chipColor, labelText) = when {
        amount > 0  -> Pair(CoralDanger,   "Due")
        amount < 0  -> Pair(EmeraldSuccess, "Advance")
        else        -> Pair(MutedForeground, "Settled")
    }
    Surface(
        color  = chipColor.copy(alpha = 0.12f),
        shape  = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Text(
            text     = labelText,
            color    = chipColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

// ─── Avatar initials ─────────────────────────────────────────────────────────

fun initialsOf(name: String): String =
    name.trim().split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2)
        .joinToString("")

val avatarGradients = listOf(
    listOf(Color(0xFF1A8C7A), Color(0xFF2A9EC0)),
    listOf(Color(0xFFD4962A), Color(0xFFD9503A)),
    listOf(Color(0xFF5A60C8), Color(0xFF3A4AB0)),
    listOf(Color(0xFF3DA876), Color(0xFF1A8C7A)),
    listOf(Color(0xFFD9503A), Color(0xFFD4762A)),
)
