package com.example.drunkenautopilot.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.drunkenautopilot.models.*

@Database(
    entities = [
        Episode::class,
        AudioRecording::class,
        Route::class,
        Point::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TimeStampConverters::class)
abstract class EpisodeRoomDatabase : RoomDatabase() {

    abstract fun episodeDao(): EpisodeDao
    abstract fun audioRecordingDao(): AudioRecordingDao
    abstract fun routeDao(): RouteDao
    abstract fun pointDao(): PointDao

    companion object {
        @Volatile
        private var INSTANCE: EpisodeRoomDatabase? = null

        fun getDatabase(context: Context): EpisodeRoomDatabase {
            if (INSTANCE != null) {
                return INSTANCE as EpisodeRoomDatabase
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EpisodeRoomDatabase::class.java,
                    "episode_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}