package com.ozerli.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {

    @Query("SELECT * FROM requests WHERE isDone = 0 ORDER BY urgency ASC, createdAt ASC")
    fun getOpenRequests(): Flow<List<Request>>

    @Query("SELECT * FROM requests WHERE isDone = 1 ORDER BY doneAt DESC")
    fun getDoneRequests(): Flow<List<Request>>

    @Query("SELECT * FROM requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<Request>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: Request): Long

    @Update
    suspend fun update(request: Request)

    @Delete
    suspend fun delete(request: Request)

    @Query("SELECT COUNT(*) FROM requests WHERE isDone = 0")
    fun getOpenCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM requests WHERE isDone = 1")
    fun getDoneCount(): Flow<Int>

    @Query("SELECT personName, COUNT(*) as cnt FROM requests GROUP BY personName ORDER BY cnt DESC LIMIT 5")
    fun getTopRequesters(): Flow<List<PersonCount>>

    @Query("SELECT * FROM requests WHERE isDone = 0 AND reminderAt IS NOT NULL AND reminderAt <= :now")
    suspend fun getOverdueReminders(now: Long): List<Request>
}

data class PersonCount(
    val personName: String,
    val cnt: Int
)
