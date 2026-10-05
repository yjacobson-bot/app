package com.ozerli.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE isDone = 0 ORDER BY priority ASC, createdAt ASC")
    fun getOpenTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isDone = 1 ORDER BY doneAt DESC LIMIT 30")
    fun getDoneTasks(): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isDone = 0")
    fun getOpenCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
