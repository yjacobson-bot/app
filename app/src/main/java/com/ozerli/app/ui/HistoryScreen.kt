package com.ozerli.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozerli.app.data.Request
import com.ozerli.app.ui.theme.*
import com.ozerli.app.viewmodel.RequestViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: RequestViewModel, onBack: () -> Unit) {
    val done      by viewModel.doneRequests.collectAsState()
    val dateFmt   = remember { SimpleDateFormat("dd/MM/yy", Locale.getDefault()) }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = { Text("היסטוריה", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "חזור")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
            )
        }
    ) { padding ->
        CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            if (done.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(shape = CircleShape, color = GreenLight, modifier = Modifier.size(90.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text("📋", fontSize = 38.sp) }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("עדיין לא טיפלת בשום בקשה", color = TextSecondary, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "✅  ${done.size} בקשות טופלו",
                            fontWeight = FontWeight.Bold,
                            color = GreenStrong,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(done, key = { it.id }) { req ->
                        DoneCard(req, dateFmt,
                            onReopen = { viewModel.reopenRequest(req) },
                            onDelete = { viewModel.deleteRequest(req) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DoneCard(
    req: Request,
    dateFmt: SimpleDateFormat,
    onReopen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(req.personName.take(1), fontWeight = FontWeight.Bold, color = GreenStrong)
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(req.personName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        if (req.shiur.isNotEmpty()) {
                            Spacer(Modifier.width(5.dp))
                            Text("(${req.shiur})", fontSize = 12.sp, color = TextTertiary)
                        }
                    }
                    Text(
                        req.doneAt?.let { "טופל ${dateFmt.format(Date(it))}" } ?: "",
                        fontSize = 11.sp, color = GreenStrong
                    )
                }
                Surface(color = GreenLight, shape = RoundedCornerShape(8.dp)) {
                    Text("✅", modifier = Modifier.padding(6.dp), fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(req.description, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)

            if (req.doneNote.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Surface(color = Indigo50, shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text("📝  ", fontSize = 13.sp)
                        Text(req.doneNote, fontSize = 13.sp, color = Indigo700, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Divider, thickness = 0.5.dp)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = TextTertiary)) {
                    Text("מחק", fontSize = 13.sp)
                }
                Spacer(Modifier.width(6.dp))
                OutlinedButton(
                    onClick = onReopen,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Indigo500)
                ) {
                    Icon(Icons.Outlined.Refresh, null, modifier = Modifier.size(15.dp), tint = Indigo600)
                    Spacer(Modifier.width(4.dp))
                    Text("פתח מחדש", fontSize = 13.sp, color = Indigo600)
                }
            }
        }
    }
}
