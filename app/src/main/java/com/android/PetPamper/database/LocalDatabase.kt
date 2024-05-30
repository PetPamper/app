package com.android.PetPamper.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

@androidx.room.Database(entities = [PetData::class], version = 1)
abstract class LocalDatabase: RoomDatabase() {

    abstract fun petDao(): PetDao
    companion object {
        // Singleton pattern
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "local-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}