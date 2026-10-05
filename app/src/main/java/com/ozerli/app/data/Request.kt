package com.ozerli.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Urgency {
    RED, YELLOW, GREEN
}

@Entity(tableName = "requests")
data class Request(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personName: String,
    val shiur: String = "",          // שיעור / כיתה – אופציונלי
    val description: String,
    val urgency: Urgency = Urgency.YELLOW,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderAt: Long? = null,    // מיליסניות לתזכורת (אופציונלי)
    val isDone: Boolean = false,
    val doneNote: String = "",
    val doneAt: Long? = null
)
