package com.ozerli.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozerli.app.data.Urgency
import com.ozerli.app.ui.theme.*
import com.ozerli.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val existing   = viewModel.taskToEdit
    val isEdit     = existing != null

    var title       by remember { mutableStateOf(existing?.title       ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var priority    by remember { mutableStateOf(existing?.priority    ?: Urgency.YELLOW) }
    var hasDueDate  by remember { mutableStateOf(existing?.dueDate != null) }
    var dueDays     by remember { mutableStateOf("3") }
    var titleError  by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    fun save() {
        titleError = title.isBlank()
        if (titleError) return
        val dueDate = if (hasDueDate) {
            val days = dueDays.toLongOrNull() ?: 3L
            System.currentTimeMillis() + days * 86_400_000L
        } else null

        if (isEdit && existing != null) {
            viewModel.updateTask(existing, title, description, priority, dueDate)
        } else {
            viewModel.addTask(title, description, priority, dueDate)
        }
        viewModel.clearEdit()
        onBack()
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEdit) "עריכת מטלה" else "מטלה חדשה",
                        fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.clearEdit(); onBack() }) {
                        Icon(Icons.Default.Close, contentDescription = "סגור")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = CardWhite)
            )
        }
    ) { padding ->
        CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                // ── Title ──────────────────────────────────────────────────
                TaskSectionLabel("מה צריך לעשות?")
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("כותרת המטלה") },
                    isError = titleError,
                    supportingText = if (titleError) ({ Text("שדה חובה") }) else null,
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = taskFieldColors()
                )

                // ── Description ────────────────────────────────────────────
                TaskSectionLabel("פרטים נוספים (אופציונלי)")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("הוסף תיאור...") },
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 4,
                    colors = taskFieldColors()
                )

                // ── Priority ───────────────────────────────────────────────
                TaskSectionLabel("עדיפות")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TaskPriorityOption(
                        label = "דחוף", emoji = "🔴",
                        selected = priority == Urgency.RED,
                        color = RedStrong, bgColor = RedLight,
                        modifier = Modifier.weight(1f),
                        onClick = { priority = Urgency.RED }
                    )
                    TaskPriorityOption(
                        label = "בינוני", emoji = "🟡",
                        selected = priority == Urgency.YELLOW,
                        color = AmberStrong, bgColor = AmberLight,
                        modifier = Modifier.weight(1f),
                        onClick = { priority = Urgency.YELLOW }
                    )
                    TaskPriorityOption(
                        label = "נמוכה", emoji = "🟢",
                        selected = priority == Urgency.GREEN,
                        color = GreenStrong, bgColor = GreenLight,
                        modifier = Modifier.weight(1f),
                        onClick = { priority = Urgency.GREEN }
                    )
                }

                // ── Due date ───────────────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (hasDueDate) AmberLight else Surface,
                    border = BorderStroke(1.dp, if (hasDueDate) AmberBorder else Divider)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📅", fontSize = 18.sp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("תאריך יעד", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    Text("הגדר מועד להשלמה", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                            Switch(
                                checked = hasDueDate,
                                onCheckedChange = { hasDueDate = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AmberStrong,
                                    checkedTrackColor = AmberLight
                                )
                            )
                        }
                        if (hasDueDate) {
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(color = AmberBorder)
                            Spacer(Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("בעוד", color = TextSecondary, fontSize = 14.sp)
                                OutlinedTextField(
                                    value = dueDays,
                                    onValueChange = { dueDays = it },
                                    modifier = Modifier.width(72.dp),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = taskFieldColors()
                                )
                                Text("ימים", color = TextSecondary, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // ── Save ───────────────────────────────────────────────────
                Button(
                    onClick = ::save,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenStrong)
                ) {
                    Text(
                        if (isEdit) "שמור שינויים" else "הוסף מטלה",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskSectionLabel(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary,
        letterSpacing = 0.3.sp
    )
}

@Composable
private fun TaskPriorityOption(
    label: String,
    emoji: String,
    selected: Boolean,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) bgColor else CardWhite,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) color else Divider
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) color else TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun taskFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = GreenStrong,
    unfocusedBorderColor = Divider,
    focusedLabelColor    = GreenStrong
)
