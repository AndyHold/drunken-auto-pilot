package com.example.drunkenautopilot.models

import androidx.room.TypeConverter
import java.sql.Timestamp

class TimeStampConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Timestamp? {
        return value?.let {
            Timestamp(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.time
    }
}