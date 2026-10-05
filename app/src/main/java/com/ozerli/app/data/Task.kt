package com.ozerli.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: Urgency = Urgency.YELLOW,
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isDone: Boolean = false,
    val doneAt: Long? = null
)
