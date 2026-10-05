package com.ozerli.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: RequestViewModel,
    onAddClick: () -> Unit,
    onStatsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val openRequests by viewModel.openRequests.collectAsState()
    val openCount by viewModel.openCount.collectAsState()

    var showDoneDialog by remember { mutableStateOf<Request?>(null) }
    var doneNote by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                        Column {
                            Text(
                                "עוזר לי 🤝",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            if (openCount > 0) {
                                Text(
                                    "$openCount בקשות פתוחות",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.History, contentDescription = "היסטוריה")
                    }
                    IconButton(onClick = onStatsClick) {
                        Icon(Icons.Default.BarChart, contentDescription = "סטטיסטיקות")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Default.Add, contentDescription = "הוסף") },
                text = { Text("בקשה חדשה") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            if (openRequests.isEmpty()) {
                EmptyState(Modifier.padding(paddingValues))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(openRequests, key = { it.id }) { request ->
                        RequestCard(
                            request = request,
                            onDoneClick = {
                                showDoneDialog = request
                                doneNote = ""
                            },
                            onDeleteClick = { viewModel.deleteRequest(request) },
                            onUrgencyChange = { urgency -> viewModel.updateUrgency(request, urgency) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    // דיאלוג סיום טיפול
    showDoneDialog?.let { request ->
        AlertDialog(
            onDismissRequest = { showDoneDialog = null },
            title = {
                Text("סיום טיפול", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
            },
            text = {
                Column {
                    Text(
                        "סימון כטופל: ${request.personName}",
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = doneNote,
                        onValueChange = { doneNote = it },
                        label = { Text("הערה (אופציונלי)") },
                        placeholder = { Text("מה עשית?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.markDone(request, doneNote)
                    showDoneDialog = null
                }) {
                    Text("טיפלתי ✓")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDoneDialog = null }) {
                    Text("ביטול")
                }
            }
        )
    }
}

@Composable
fun RequestCard(
    request: Request,
    onDoneClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onUrgencyChange: (Urgency) -> Unit
) {
    val urgencyColor = when (request.urgency) {
        Urgency.RED -> UrgencyRed
        Urgency.YELLOW -> UrgencyYellow
        Urgency.GREEN -> UrgencyGreen
    }
    val urgencyBg = when (request.urgency) {
        Urgency.RED -> UrgencyRedLight
        Urgency.YELLOW -> UrgencyYellowLight
        Urgency.GREEN -> UrgencyGreenLight
    }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()) }

    var showUrgencyMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // פס צבע צד שמאל (RTL = ימין)
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(urgencyColor, RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                // שורה ראשונה: שם + שיעור + תג דחיפות
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            request.personName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (request.shiur.isNotEmpty()) {
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    request.shiur,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // תג דחיפות – ניתן ללחיצה לשינוי
                    Box {
                        Surface(
                            onClick = { showUrgencyMenu = true },
                            color = urgencyBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                when (request.urgency) {
                                    Urgency.RED -> "🔴 דחוף"
                                    Urgency.YELLOW -> "🟡 בינוני"
                                    Urgency.GREEN -> "🟢 רגיל"
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 12.sp,
                                color = urgencyColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        DropdownMenu(
                            expanded = showUrgencyMenu,
                            onDismissRequest = { showUrgencyMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🔴 דחוף") },
                                onClick = { onUrgencyChange(Urgency.RED); showUrgencyMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("🟡 בינוני") },
                                onClick = { onUrgencyChange(Urgency.YELLOW); showUrgencyMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("🟢 רגיל") },
                                onClick = { onUrgencyChange(Urgency.GREEN); showUrgencyMenu = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // תיאור
                Text(
                    request.description,
                    fontSize = 14.sp,
                    color = Color(0xFF444444),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                // שורה תחתונה: תאריך + כפתורים
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        dateFormat.format(Date(request.createdAt)),
                        fontSize = 11.sp,
                        color = Color(0xFF888888)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // מחיקה
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "מחק",
                                tint = Color(0xFFBBBBBB),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // סיום טיפול
                        Button(
                            onClick = onDoneClick,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("טיפלתי", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("מחיקה") },
            text = { Text("למחוק את הבקשה של ${request.personName}?") },
            confirmButton = {
                Button(
                    onClick = { onDeleteClick(); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = UrgencyRed)
                ) { Text("מחק") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("ביטול") }
            }
        )
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "אין בקשות פתוחות!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "לחץ על + כדי להוסיף בקשה חדשה",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
