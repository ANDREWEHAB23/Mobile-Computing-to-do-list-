package com.example.todolist.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>//estakhdemt Flow 3lshan ellist t3mel update automatically mngheir refresh
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?
    @Update
    suspend fun update(task: Task)
    @Delete
    suspend fun delete(task: Task)
    @Query("DELETE FROM tasks WHERE isDone = 1")//hamsah eltasks elly khelst
    suspend fun deleteCompleted()
    @Query("""
        SELECT * FROM tasks
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)//like bykhaleny acheck 3lapart mn eltext msh lazm kolo yeb'a nfso
    fun search(query: String): Flow<List<Task>>
}
