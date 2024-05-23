package com.android.PetPamper.database

import androidx.room.RoomDatabase

@androidx.room.Database(entities = [PetData::class], version = 1)
abstract class LocalDatabase: RoomDatabase() {

}