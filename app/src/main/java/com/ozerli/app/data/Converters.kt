package com.ozerli.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromUrgency(urgency: Urgency): String = urgency.name

    @TypeConverter
    fun toUrgency(value: String): Urgency = Urgency.valueOf(value)
}
