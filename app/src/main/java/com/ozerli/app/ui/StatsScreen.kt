package com.ozerli.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozerli.app.ui.theme.*
import com.ozerli.app.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: RequestViewModel, onBack: () -> Unit) {
    val openCount      by viewModel.openCount.collectAsState()
    val doneCount      by viewModel.doneCount.collectAsState()
    val topRequesters  by viewModel.topRequesters.collectAsState()

    val total       = openCount + doneCount
    val donePct     = if (total > 0) doneCount.toFloat() / total else 0f

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = { Text("סטטיסטיקות", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "חזור") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
            )
        }
    ) { padding ->
        CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                if (total == 0) {
                    Spacer(Modifier.height(60.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(shape = CircleShape, color = Indigo50, modifier = Modifier.size(90.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text("📊", fontSize = 38.sp) }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("אין נתונים עדיין", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("הוסף בקשות כדי לראות סטטיסטיקות", fontSize = 14.sp, color = TextTertiary, textAlign = TextAlign.Center)
                    }
                    return@Column
                }

                // ── 3 stat tiles ──────────────────────────────────────────
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BigStatTile(modifier = Modifier.weight(1f), value = "$openCount", label = "פתוחות",
                        emoji = "📋", gradientColors = listOf(Color(0xFFFF6B35), Color(0xFFFF8C42)))
                    BigStatTile(modifier = Modifier.weight(1f), value = "$doneCount", label = "טופלו",
                        emoji = "✅", gradientColors = listOf(GreenStrong, Color(0xFF34D399)))
                    BigStatTile(modifier = Modifier.weight(1f), value = "$total",     label = "סה״כ",
                        emoji = "📈", gradientColors = listOf(Indigo700, Indigo500))
                }

                // ── Progress card ─────────────────────────────────────────
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(CardWhite), elevation = CardDefaults.cardElevation(1.dp)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("אחוז טיפול", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "${(donePct * 100).toInt()}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Indigo600
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(Indigo50)) {
                            if (donePct > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(donePct)
                                        .fillMaxHeight()
                                        .background(Brush.horizontalGradient(listOf(Indigo700, Indigo500)), RoundedCornerShape(5.dp))
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("$doneCount מתוך $total בקשות טופלו", fontSize = 12.sp, color = TextTertiary)
                    }
                }

                // ── Top requesters ────────────────────────────────────────
                if (topRequesters.isNotEmpty()) {
                    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(CardWhite), elevation = CardDefaults.cardElevation(1.dp)) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("🏆  הפונים הכי הרבה", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(14.dp))

                            val maxCnt = topRequesters.firstOrNull()?.cnt ?: 1
                            val medals = listOf("🥇","🥈","🥉","4️⃣","5️⃣")

                            topRequesters.forEachIndexed { i, item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(medals.getOrElse(i) { "·" }, fontSize = 20.sp)
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(item.personName, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                            Text("${item.cnt} פניות", fontSize = 12.sp, color = TextTertiary)
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Indigo50)) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(item.cnt.toFloat() / maxCnt)
                                                    .fillMaxHeight()
                                                    .background(Brush.horizontalGradient(listOf(Indigo700, Indigo500)), RoundedCornerShape(3.dp))
                                            )
                                        }
                                    }
                                }
                                if (i < topRequesters.lastIndex) Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BigStatTile(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    emoji: String,
    gradientColors: List<Color>
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(gradientColors))
            .padding(14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, color = Color.White)
            Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
        }
    }
}
