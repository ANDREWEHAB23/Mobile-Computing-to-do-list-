package com.example.todolist.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val priority: String,
    val dueDate: String,
    val isCompleted: Boolean = false
)
