package com.example.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.local.AppDatabase
import com.example.todolist.data.local.TaskEntity
import com.example.todolist.data.repository.TaskRepository
import com.example.todolist.model.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val dao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
    }

    val tasks: StateFlow<List<Task>> = repository.allTasks
        .map { entities: List<TaskEntity> ->
            entities.map { it.toTask() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList<Task>()
        )

    fun addTask(task: Task) {
        viewModelScope.launch { repository.insert(task.toTaskEntity()) }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repository.update(task.toTaskEntity()) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.delete(task.toTaskEntity()) }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.update(task.copy(isCompleted = !task.isCompleted).toTaskEntity())
        }
    }
}

fun TaskEntity.toTask(): Task = Task(
    id = this.id,
    title = this.title,
    description = this.description,
    priority = this.priority,
    dueDate = this.dueDate,
    isCompleted = this.isCompleted
)

fun Task.toTaskEntity(): TaskEntity = TaskEntity(
    id = this.id,
    title = this.title,
    description = this.description,
    priority = this.priority,
    dueDate = this.dueDate,
    isCompleted = this.isCompleted
)