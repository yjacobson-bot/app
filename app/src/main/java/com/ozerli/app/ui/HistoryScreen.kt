package com.ozerli.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozerli.app.data.Request
import com.ozerli.app.viewmodel.RequestViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: RequestViewModel,
    onBack: () -> Unit
) {
    val doneRequests by viewModel.doneRequests.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("היסטוריה", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "חזור")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            if (doneRequests.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("📋", fontSize = 56.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("עדיין לא טיפלת בשום בקשה", fontSize = 18.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "✅ ${doneRequests.size} בקשות טופלו",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    items(doneRequests, key = { it.id }) { request ->
                        DoneRequestCard(
                            request = request,
                            dateFormat = dateFormat,
                            onReopen = { viewModel.reopenRequest(request) },
                            onDelete = { viewModel.deleteRequest(request) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoneRequestCard(
    request: Request,
    dateFormat: SimpleDateFormat,
    onReopen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FFF8))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✅", fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(request.personName, fontWeight = FontWeight.Bold)
                    if (request.shiur.isNotEmpty()) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "(${request.shiur})",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                Text(
                    request.doneAt?.let { dateFormat.format(Date(it)) } ?: "",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(4.dp))
            Text(request.description, fontSize = 13.sp, color = Color(0xFF555555))

            if (request.doneNote.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "📝 ${request.doneNote}",
                    fontSize = 12.sp,
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFBBBBBB))
                ) {
                    Text("מחק", fontSize = 12.sp)
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onReopen,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("פתח מחדש", fontSize = 12.sp)
                }
            }
        }
    }
}
