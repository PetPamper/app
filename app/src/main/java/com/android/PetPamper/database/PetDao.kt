package com.android.PetPamper.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PetDao {
    @Query ("SELECT * FROM petdata WHERE id = :petId")
    fun getPet(petId: String): PetData

    @Query ("SELECT * FROM petdata WHERE ownerId = :ownerId")
    fun getPetsFromOwner(ownerId: String): List<PetData>

    @Insert
    fun insert(petData: PetData)

    @Update
    fun update(petData: PetData)
}