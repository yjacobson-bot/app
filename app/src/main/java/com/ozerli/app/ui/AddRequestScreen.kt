package com.ozerli.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozerli.app.data.Urgency
import com.ozerli.app.ui.theme.*
import com.ozerli.app.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRequestScreen(
    viewModel: RequestViewModel,
    onBack: () -> Unit
) {
    var personName   by remember { mutableStateOf("") }
    var shiur        by remember { mutableStateOf("") }
    var phone        by remember { mutableStateOf("") }
    var description  by remember { mutableStateOf("") }
    var urgency      by remember { mutableStateOf(Urgency.YELLOW) }
    var hasReminder  by remember { mutableStateOf(false) }
    var reminderDays by remember { mutableStateOf("2") }

    var nameError by remember { mutableStateOf(false) }
    var descError by remember { mutableStateOf(false) }

    fun save() {
        nameError = personName.isBlank()
        descError = description.isBlank()
        if (nameError || descError) return
        val reminderAt = if (hasReminder) {
            val days = reminderDays.toLongOrNull() ?: 2L
            System.currentTimeMillis() + days * 86_400_000L
        } else null
        viewModel.addRequest(personName, shiur, phone, description, urgency, reminderAt)
        onBack()
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("בקשה חדשה", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                // ── Name ─────────────────────────────────────────────────
                SectionLabel("שם האדם")
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it; nameError = false },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("לדוגמה: יוסי כהן") },
                    isError = nameError,
                    supportingText = if (nameError) ({ Text("שדה חובה") }) else null,
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = fieldColors()
                )

                // ── Shiur ─────────────────────────────────────────────────
                SectionLabel("שיעור (אופציונלי)")
                OutlinedTextField(
                    value = shiur,
                    onValueChange = { shiur = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("א', ב', ג'...") },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = fieldColors()
                )

                // ── Phone ─────────────────────────────────────────────────
                SectionLabel("טלפון (אופציונלי)")
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("050-0000000") },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = fieldColors()
                )

                // ── Description ───────────────────────────────────────────
                SectionLabel("במה הוא צריך עזרה?")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; descError = false },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("תאר בקצרה מה נדרש...") },
                    isError = descError,
                    supportingText = if (descError) ({ Text("שדה חובה") }) else null,
                    shape = RoundedCornerShape(14.dp),
                    minLines = 3,
                    maxLines = 6,
                    colors = fieldColors()
                )

                // ── Urgency ───────────────────────────────────────────────
                SectionLabel("דרגת דחיפות")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UrgencyOption(
                        label = "דחוף", emoji = "🔴",
                        selected = urgency == Urgency.RED,
                        color = RedStrong, bgColor = RedLight,
                        modifier = Modifier.weight(1f),
                        onClick = { urgency = Urgency.RED }
                    )
                    UrgencyOption(
                        label = "בינוני", emoji = "🟡",
                        selected = urgency == Urgency.YELLOW,
                        color = AmberStrong, bgColor = AmberLight,
                        modifier = Modifier.weight(1f),
                        onClick = { urgency = Urgency.YELLOW }
                    )
                    UrgencyOption(
                        label = "רגיל", emoji = "🟢",
                        selected = urgency == Urgency.GREEN,
                        color = GreenStrong, bgColor = GreenLight,
                        modifier = Modifier.weight(1f),
                        onClick = { urgency = Urgency.GREEN }
                    )
                }

                // ── Reminder ──────────────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Indigo50,
                    border = BorderStroke(1.dp, if (hasReminder) Indigo500 else Divider)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔔", fontSize = 18.sp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("תזכורת", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    Text(
                                        "הזכר לי לחזור לענין הזה",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Switch(
                                checked = hasReminder,
                                onCheckedChange = { hasReminder = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Indigo600,
                                    checkedTrackColor = Indigo100
                                )
                            )
                        }
                        if (hasReminder) {
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(color = Divider)
                            Spacer(Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("תזכיר לי אחרי", color = TextSecondary, fontSize = 14.sp)
                                OutlinedTextField(
                                    value = reminderDays,
                                    onValueChange = { reminderDays = it },
                                    modifier = Modifier.width(72.dp),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = fieldColors()
                                )
                                Text("ימים", color = TextSecondary, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // ── Save button ───────────────────────────────────────────
                Button(
                    onClick = ::save,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("הוסף בקשה", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary,
        letterSpacing = 0.3.sp
    )
}

@Composable
private fun UrgencyOption(
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
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Indigo500,
    unfocusedBorderColor = Divider,
    focusedLabelColor    = Indigo500
)
