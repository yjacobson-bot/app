package com.ozerli.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozerli.app.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: RequestViewModel,
    onBack: () -> Unit
) {
    val openCount by viewModel.openCount.collectAsState()
    val doneCount by viewModel.doneCount.collectAsState()
    val topRequesters by viewModel.topRequesters.collectAsState()

    val total = openCount + doneCount
    val donePercent = if (total > 0) (doneCount.toFloat() / total * 100).toInt() else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("סטטיסטיקות", fontWeight = FontWeight.Bold) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // כרטיסי סיכום
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "$openCount",
                        label = "פתוחות",
                        emoji = "📋",
                        bgColor = Color(0xFFFFF3E0)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "$doneCount",
                        label = "טופלו",
                        emoji = "✅",
                        bgColor = Color(0xFFE8F5E9)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "$donePercent%",
                        label = "אחוז טיפול",
                        emoji = "🏆",
                        bgColor = Color(0xFFE3F2FD)
                    )
                }

                // סרגל התקדמות
                if (total > 0) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("אחוז הצלחה", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { if (total > 0) doneCount.toFloat() / total else 0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color(0xFFE0E0E0)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "$doneCount מתוך $total בקשות טופלו",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // מי פונה הכי הרבה
                if (topRequesters.isNotEmpty()) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🌟 הפונים הכי הרבה", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(12.dp))

                            val maxCount = topRequesters.firstOrNull()?.cnt ?: 1

                            topRequesters.forEachIndexed { index, item ->
                                val medals = listOf("🥇", "🥈", "🥉", "4.", "5.")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(medals.getOrElse(index) { "${index + 1}." }, fontSize = 18.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.personName, fontWeight = FontWeight.Medium)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(item.cnt.toFloat() / maxCount)
                                                    .height(6.dp)
                                                    .background(
                                                        MaterialTheme.colorScheme.primary,
                                                        RoundedCornerShape(3.dp)
                                                    )
                                            )
                                            if (item.cnt.toFloat() / maxCount < 1f) {
                                                Box(modifier = Modifier.weight(1f - item.cnt.toFloat() / maxCount))
                                            }
                                        }
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "${item.cnt} פניות",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                                if (index < topRequesters.size - 1) Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                }

                if (total == 0) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📊", fontSize = 56.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("אין נתונים עדיין", fontSize = 18.sp, color = Color.Gray)
                        Text("הוסף בקשות כדי לראות סטטיסטיקות", fontSize = 14.sp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    emoji: String,
    bgColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
