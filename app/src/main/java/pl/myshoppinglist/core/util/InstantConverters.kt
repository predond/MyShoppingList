package pl.myshoppinglist.core.util

import androidx.room.TypeConverter

class InstantConverters {
    @TypeConverter fun fromEpoch(millis: Long?): java.time.Instant? = millis?.let { java.time.Instant.ofEpochMilli(it) }
    @TypeConverter fun toEpoch(instant: java.time.Instant?): Long? = instant?.toEpochMilli()
}
