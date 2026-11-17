package com.example.todolist.data.local
import com.example.todolist.data.local.Task
import com.example.todolist.data.local.TaskDao
import kotlinx.coroutines.flow.Flow
class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> =
        taskDao.getAllTasks()
    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }
    suspend fun update(task: Task) {
        taskDao.update(task)
    }
    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }
    suspend fun deleteCompleted() {
        taskDao.deleteCompleted()
    }
    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)
    }
    fun search(query: String): Flow<List<Task>> {
        return taskDao.search(query)
    }
}
