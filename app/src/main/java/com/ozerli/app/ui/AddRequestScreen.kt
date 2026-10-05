package com.ozerli.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ozerli.app.data.Urgency
import com.ozerli.app.ui.theme.UrgencyGreen
import com.ozerli.app.ui.theme.UrgencyRed
import com.ozerli.app.ui.theme.UrgencyYellow
import com.ozerli.app.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRequestScreen(
    viewModel: RequestViewModel,
    onBack: () -> Unit
) {
    var personName by remember { mutableStateOf("") }
    var shiur by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf(Urgency.YELLOW) }
    var hasReminder by remember { mutableStateOf(false) }
    var reminderDays by remember { mutableStateOf("2") }

    var nameError by remember { mutableStateOf(false) }
    var descError by remember { mutableStateOf(false) }

    fun save() {
        nameError = personName.isBlank()
        descError = description.isBlank()
        if (nameError || descError) return

        val reminderAt = if (hasReminder) {
            val days = reminderDays.toLongOrNull() ?: 2L
            System.currentTimeMillis() + days * 24 * 60 * 60 * 1000L
        } else null

        viewModel.addRequest(personName, shiur, description, urgency, reminderAt)
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("בקשה חדשה", fontWeight = FontWeight.Bold) },
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

                // שם האדם
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it; nameError = false },
                    label = { Text("שם האדם *") },
                    placeholder = { Text("יוסי כהן") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError,
                    supportingText = if (nameError) ({ Text("נא להזין שם") }) else null,
                    singleLine = true
                )

                // שיעור (אופציונלי)
                OutlinedTextField(
                    value = shiur,
                    onValueChange = { shiur = it },
                    label = { Text("שיעור (אופציונלי)") },
                    placeholder = { Text("א', ב', תיכון...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // תיאור הבקשה
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; descError = false },
                    label = { Text("מה הוא צריך? *") },
                    placeholder = { Text("תאר את הבקשה בקצרה...") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = descError,
                    supportingText = if (descError) ({ Text("נא להזין תיאור") }) else null,
                    minLines = 3,
                    maxLines = 6
                )

                // דחיפות
                Text("דרגת דחיפות:", fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UrgencyChip(
                        label = "🔴 דחוף",
                        selected = urgency == Urgency.RED,
                        color = UrgencyRed,
                        onClick = { urgency = Urgency.RED },
                        modifier = Modifier.weight(1f)
                    )
                    UrgencyChip(
                        label = "🟡 בינוני",
                        selected = urgency == Urgency.YELLOW,
                        color = UrgencyYellow,
                        onClick = { urgency = Urgency.YELLOW },
                        modifier = Modifier.weight(1f)
                    )
                    UrgencyChip(
                        label = "🟢 רגיל",
                        selected = urgency == Urgency.GREEN,
                        color = UrgencyGreen,
                        onClick = { urgency = Urgency.GREEN },
                        modifier = Modifier.weight(1f)
                    )
                }

                // תזכורת
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🔔 הגדר תזכורת", fontWeight = FontWeight.Medium)
                            Switch(
                                checked = hasReminder,
                                onCheckedChange = { hasReminder = it }
                            )
                        }

                        if (hasReminder) {
                            Spacer(Modifier.height(8.dp))
                            Row(
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("תזכיר לי אחרי")
                                OutlinedTextField(
                                    value = reminderDays,
                                    onValueChange = { reminderDays = it },
                                    modifier = Modifier.width(70.dp),
                                    singleLine = true
                                )
                                Text("ימים")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // כפתור שמירה
                Button(
                    onClick = ::save,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("הוסף בקשה ✓", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun UrgencyChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = if (selected) color.copy(alpha = 0.15f) else Color(0xFFF5F5F5),
        border = if (selected)
            androidx.compose.foundation.BorderStroke(2.dp, color)
        else
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD))
    ) {
        Text(
            label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = if (selected) color else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = MaterialTheme.typography.bodySmall.fontSize
        )
    }
}
