package com.example.drunkenautopilot.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.drunkenautopilot.models.*

@Database(entities = [Episode::class, VideoRecording::class, AudioRecording::class], version = 1, exportSchema = false)
public abstract class EpisodeRoomDatabase: RoomDatabase() {

    abstract fun episodeDao(): EpisodeDao
    abstract fun videoRecordingDao(): VideoRecordingDao
    abstract fun audioRecordingDao(): AudioRecordingDao

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