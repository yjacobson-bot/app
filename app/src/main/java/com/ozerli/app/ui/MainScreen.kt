package com.ozerli.app.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozerli.app.data.Request
import com.ozerli.app.data.Urgency
import com.ozerli.app.ui.theme.*
import com.ozerli.app.viewmodel.RequestViewModel
import com.ozerli.app.viewmodel.SortMode
import com.ozerli.app.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: RequestViewModel,
    taskViewModel: TaskViewModel,
    onAddRequestClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onStatsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val openRequests  by viewModel.openRequests.collectAsState()
    val openCount     by viewModel.openCount.collectAsState()
    val doneCount     by viewModel.doneCount.collectAsState()
    val sortMode      by viewModel.sortMode.collectAsState()
    val taskOpenCount by taskViewModel.openCount.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0=requests, 1=tasks
    var doneDialogRequest by remember { mutableStateOf<Request?>(null) }
    var doneNote          by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Surface,
        bottomBar = {
            BottomAppBarRow(
                onStatsClick   = onStatsClick,
                onHistoryClick = onHistoryClick,
                onAddClick     = { if (activeTab == 0) onAddRequestClick() else onAddTaskClick() },
                openCount      = openCount,
                taskOpenCount  = taskOpenCount
            )
        }
    ) { padding ->

        CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                // ── Header ──────────────────────────────────────────────────
                item {
                    HeaderCard(openCount = openCount, doneCount = doneCount, taskOpenCount = taskOpenCount)
                }

                // ── Tab switcher ─────────────────────────────────────────────
                item {
                    TabSwitcher(activeTab = activeTab, onTabChange = { activeTab = it })
                }

                // ── Personal tasks tab ───────────────────────────────────────
                if (activeTab == 1) {
                    item {
                        PersonalTasksTab(viewModel = taskViewModel)
                    }
                    return@LazyColumn
                }

                // ── Empty state ──────────────────────────────────────────────
                if (openRequests.isEmpty()) {
                    item { EmptyStateBlock() }
                } else {
                    // ── Section label + sort toggle ──────────────────────────
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "בקשות פתוחות",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Surface(
                                onClick = { viewModel.toggleSort() },
                                shape = RoundedCornerShape(10.dp),
                                color = Indigo50
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        if (sortMode == SortMode.DATE) Icons.Default.CalendarToday else Icons.Default.PriorityHigh,
                                        contentDescription = "מיון",
                                        modifier = Modifier.size(13.dp),
                                        tint = Indigo600
                                    )
                                    Text(
                                        if (sortMode == SortMode.DATE) "לפי תאריך" else "לפי דחיפות",
                                        fontSize = 11.sp,
                                        color = Indigo600,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    // ── Cards ────────────────────────────────────────────────
                    items(openRequests, key = { it.id }) { req ->
                        RequestCard(
                            request = req,
                            onDoneClick = { doneDialogRequest = req; doneNote = "" },
                            onDeleteClick = { viewModel.deleteRequest(req) },
                            onUrgencyChange = { viewModel.updateUrgency(req, it) }
                        )
                    }
                }
            }
        }
    }

    // ── Done dialog ────────────────────────────────────────────────────────
    doneDialogRequest?.let { req ->
        AlertDialog(
            onDismissRequest = { doneDialogRequest = null },
            shape = RoundedCornerShape(24.dp),
            containerColor = CardWhite,
            title = {
                CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                    Text("✅  סיימתי לטפל", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                    Column {
                        Surface(
                            color = Indigo50,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                req.personName + if (req.shiur.isNotEmpty()) " · ${req.shiur}" else "",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontWeight = FontWeight.Medium,
                                color = Indigo700
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = doneNote,
                            onValueChange = { doneNote = it },
                            label = { Text("הערה קצרה (אופציונלי)") },
                            placeholder = { Text("מה עשית?") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 2
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.markDone(req, doneNote); doneDialogRequest = null },
                    shape = RoundedCornerShape(14.dp)
                ) { Text("שמור") }
            },
            dismissButton = {
                TextButton(onClick = { doneDialogRequest = null }) { Text("ביטול") }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header card with gradient
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabSwitcher(activeTab: Int, onTabChange: (Int) -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(14.dp),
            color = Indigo50
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                listOf("🤝  בקשות עזרה", "✅  מטלות שלי").forEachIndexed { index, label ->
                    Surface(
                        onClick = { onTabChange(index) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeTab == index) CardWhite else Color.Transparent,
                        shadowElevation = if (activeTab == index) 2.dp else 0.dp
                    ) {
                        Text(
                            label,
                            modifier = Modifier.padding(vertical = 10.dp),
                            fontSize = 13.sp,
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == index) Indigo700 else TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCard(openCount: Int, doneCount: Int, taskOpenCount: Int = 0) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(listOf(Indigo700, Indigo500))
            )
            .padding(24.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🤝", fontSize = 28.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "עוזר לי",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "מנהל בקשות עזרה אישי",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatPill(label = "בקשות",  value = "$openCount",     color = Color.White)
                    StatPill(label = "מטלות",  value = "$taskOpenCount", color = Color.White.copy(alpha = 0.8f))
                    StatPill(label = "טופלו",  value = "$doneCount",     color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color) {
    Surface(
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 18.sp)
            Text(label,  color = color.copy(alpha = 0.8f), fontSize = 13.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Request card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun RequestCard(
    request: Request,
    onDoneClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onUrgencyChange: (Urgency) -> Unit
) {
    val (urgencyColor, urgencyBg, urgencyLabel) = when (request.urgency) {
        Urgency.RED    -> Triple(RedStrong,   RedLight,   "דחוף")
        Urgency.YELLOW -> Triple(AmberStrong, AmberLight, "בינוני")
        Urgency.GREEN  -> Triple(GreenStrong, GreenLight, "רגיל")
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var showUrgencyMenu  by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, hoveredElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Row 1: avatar + name + badge ──────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Initials avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Indigo100),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        request.personName.take(1),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Indigo700
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            request.personName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        if (request.shiur.isNotEmpty()) {
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                color = Indigo50,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    request.shiur,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    color = Indigo600,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            dateFormat.format(Date(request.createdAt)),
                            fontSize = 11.sp,
                            color = TextTertiary
                        )
                        if (request.phone.isNotEmpty()) {
                            Text("·", fontSize = 11.sp, color = TextTertiary)
                            Text(
                                request.phone,
                                fontSize = 11.sp,
                                color = Indigo600
                            )
                        }
                    }
                }

                // Urgency badge (tappable)
                Box {
                    Surface(
                        onClick = { keyboardController?.hide(); focusManager.clearFocus(); showUrgencyMenu = true },
                        color = urgencyBg,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(urgencyColor)
                            )
                            Text(urgencyLabel, fontSize = 12.sp, color = urgencyColor, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    DropdownMenu(expanded = showUrgencyMenu, onDismissRequest = { showUrgencyMenu = false }) {
                        listOf(
                            Urgency.RED    to "🔴 דחוף",
                            Urgency.YELLOW to "🟡 בינוני",
                            Urgency.GREEN  to "🟢 רגיל"
                        ).forEach { (u, label) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = {
                                onUrgencyChange(u); showUrgencyMenu = false
                            })
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Description ───────────────────────────────────────────────
            Text(
                request.description,
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Divider, thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            // ── Actions row ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "מחק",
                        tint = TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Button(
                    onClick = onDoneClick,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("טיפלתי", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("מחיקת בקשה") },
            text  = { Text("למחוק את הבקשה של ${request.personName}?") },
            confirmButton = {
                Button(
                    onClick = { onDeleteClick(); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RedStrong),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("מחק") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("ביטול") }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyStateBlock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Indigo100,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🎉", fontSize = 44.sp)
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("אין בקשות פתוחות!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(6.dp))
        Text("לחץ + כדי להוסיף בקשה חדשה", fontSize = 14.sp, color = TextTertiary, textAlign = TextAlign.Center)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom navigation bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun BottomAppBarRow(
    onStatsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onAddClick: () -> Unit,
    openCount: Int,
    taskOpenCount: Int = 0
) {
    Surface(
        shadowElevation = 16.dp,
        color = CardWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stats
            NavItem(icon = Icons.Default.BarChart, label = "סטטיסטיקות", onClick = onStatsClick)

            // FAB center
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape,
                containerColor = Indigo600,
                contentColor = Color.White,
                modifier = Modifier.size(58.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "הוסף", modifier = Modifier.size(26.dp))
            }

            // History
            NavItem(icon = Icons.Default.History, label = "היסטוריה", onClick = onHistoryClick)
        }
    }
}

@Composable
private fun NavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = label, tint = TextSecondary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 11.sp, color = TextSecondary)
    }
}
