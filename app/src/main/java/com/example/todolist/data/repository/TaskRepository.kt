package com.example.todolist.data.repository

import com.example.todolist.data.local.TaskDao
import com.example.todolist.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun insert(task: TaskEntity) = taskDao.insertTask(task)
    suspend fun update(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun delete(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun deleteCompleted() = taskDao.deleteCompletedTasks()
    suspend fun getTask(id: Int): TaskEntity? = taskDao.getTaskById(id)
    fun searchTasks(query: String) = taskDao.searchTasks(query)
}
