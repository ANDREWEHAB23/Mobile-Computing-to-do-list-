package com.example.todolist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val isDone: Boolean = false,
    val priority: String = "MEDIUM",
    val dueDate: Long? = null, //long 3lshan hnstore eldate ktimestamp
    val createdAt: Long = System.currentTimeMillis() //wa't ma 3amlna create lltask be elmillisecond
)
