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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozerli.app.data.Task
import com.ozerli.app.data.Urgency
import com.ozerli.app.ui.theme.*
import com.ozerli.app.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PersonalTasksTab(viewModel: TaskViewModel) {
    val openTasks by viewModel.openTasks.collectAsState()
    val doneTasks by viewModel.doneTasks.collectAsState()
    var showDone  by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Open tasks
            if (openTasks.isEmpty() && doneTasks.isEmpty()) {
                item { TaskEmptyState() }
            } else {
                if (openTasks.isNotEmpty()) {
                    item {
                        Text(
                            "לביצוע",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    items(openTasks, key = { "open_${it.id}" }) { task ->
                        TaskCard(
                            task = task,
                            onDoneClick = { viewModel.markDone(task) },
                            onDeleteClick = { viewModel.deleteTask(task) },
                            onPriorityChange = { viewModel.updatePriority(task, it) }
                        )
                    }
                }

                // Done section
                if (doneTasks.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDone = !showDone }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    "הושלמו",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = TextTertiary,
                                    letterSpacing = 0.5.sp
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = GreenLight
                                ) {
                                    Text(
                                        "${doneTasks.size}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        color = GreenStrong,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Icon(
                                if (showDone) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    if (showDone) {
                        items(doneTasks, key = { "done_${it.id}" }) { task ->
                            TaskCard(
                                task = task,
                                onDoneClick = { viewModel.reopenTask(task) },
                                onDeleteClick = { viewModel.deleteTask(task) },
                                onPriorityChange = { viewModel.updatePriority(task, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onDoneClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPriorityChange: (Urgency) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yy", Locale.getDefault()) }
    val (priorityColor, priorityBg) = when (task.priority) {
        Urgency.RED    -> Pair(RedStrong,   RedLight)
        Urgency.YELLOW -> Pair(AmberStrong, AmberLight)
        Urgency.GREEN  -> Pair(GreenStrong, GreenLight)
    }
    var showPriorityMenu  by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isDone) Color(0xFFF9FAFB) else CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isDone) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Priority bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (task.isDone) Divider else priorityColor)
            )

            // Checkbox
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (task.isDone) GreenLight else priorityBg)
                    .clickable { onDoneClick() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isDone) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = GreenStrong,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(priorityColor.copy(alpha = 0.5f))
                    )
                }
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = if (task.isDone) TextTertiary else TextPrimary,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        task.description,
                        fontSize = 12.sp,
                        color = TextTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        dateFormat.format(Date(task.createdAt)),
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                    task.dueDate?.let { due ->
                        val isOverdue = !task.isDone && due < System.currentTimeMillis()
                        Text("·", fontSize = 11.sp, color = TextTertiary)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = if (isOverdue) RedStrong else TextTertiary
                            )
                            Text(
                                dateFormat.format(Date(due)),
                                fontSize = 11.sp,
                                color = if (isOverdue) RedStrong else TextTertiary,
                                fontWeight = if (isOverdue) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Priority badge + delete
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box {
                    Surface(
                        onClick = { showPriorityMenu = true },
                        shape = RoundedCornerShape(8.dp),
                        color = if (task.isDone) Color(0xFFF3F4F6) else priorityBg
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .padding(2.dp)
                        )
                        // Just the dot indicator
                        Box(
                            modifier = Modifier
                                .size(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (task.isDone) Divider else priorityColor)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = showPriorityMenu,
                        onDismissRequest = { showPriorityMenu = false }
                    ) {
                        listOf(
                            Urgency.RED    to "🔴 דחוף",
                            Urgency.YELLOW to "🟡 בינוני",
                            Urgency.GREEN  to "🟢 רגיל"
                        ).forEach { (u, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = { onPriorityChange(u); showPriorityMenu = false }
                            )
                        }
                    }
                }
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "מחק",
                        tint = TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("מחיקת מטלה") },
            text  = { Text("למחוק את \"${task.title}\"?") },
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

@Composable
private fun TaskEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = GreenLight,
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("✅", fontSize = 38.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("אין מטלות פתוחות!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(6.dp))
        Text("לחץ + כדי להוסיף מטלה חדשה", fontSize = 13.sp, color = TextTertiary)
    }
}
