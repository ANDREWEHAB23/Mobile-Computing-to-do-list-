package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todolist.model.Task
import com.example.todolist.ui.components.AddEditTaskDialog
import com.example.todolist.ui.components.TaskListContent
import com.example.todolist.ui.theme.ToDoListTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ToDoListTheme {
                // Local UI state using remember and mutableStateOf
                var tasks by remember {
                    mutableStateOf(
                        listOf(
                            Task(1, "Mobile Computing Project", "Implement Compose UI and Intents", "High", "2025-06-15", false),
                            Task(2, "Study Kotlin", "Learn Coroutines and Flow", "Medium", "2025-06-18", true),
                            Task(3, "Buy Groceries", "Milk, eggs, and bread", "Low", "2025-06-10", false)
                        )
                    )
                }

                var isDialogVisible by remember { mutableStateOf(false) }
                var taskToEdit by remember { mutableStateOf<Task?>(null) }

                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "To-Do List",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                taskToEdit = null
                                isDialogVisible = true
                            },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Task")
                        }
                    }
                ) { innerPadding ->
                    TaskListContent(
                        tasks = tasks,
                        onToggleCompletion = { taskId ->
                            tasks = tasks.map {
                                if (it.id == taskId) {
                                    val updated = it.copy(isCompleted = !it.isCompleted)
                                    Toast.makeText(
                                        this,
                                        if (updated.isCompleted) "Task completed" else "Task marked active",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    updated
                                } else {
                                    it
                                }
                            }
                        },
                        onEditTask = { task ->
                            taskToEdit = task
                            isDialogVisible = true
                        },
                        onDeleteTask = { taskId ->

                            val deletedTask = tasks.find { it.id == taskId }
                            tasks = tasks.filter { it.id != taskId }


                            if (deletedTask != null) {
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Task deleted",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        tasks = tasks + deletedTask
                                    }
                                }
                            }
                        },
                        onTaskClick = { task ->
                            // --- EXPLICIT INTENT ---
                            // Explicitly specifies TaskDetailActivity as the target component
                            val intent = Intent(this, TaskDetailActivity::class.java).apply {
                                putExtra(TaskDetailActivity.EXTRA_TITLE, task.title)
                                putExtra(TaskDetailActivity.EXTRA_DESCRIPTION, task.description)
                                putExtra(TaskDetailActivity.EXTRA_PRIORITY, task.priority)
                                putExtra(TaskDetailActivity.EXTRA_DUE_DATE, task.dueDate)
                                putExtra(TaskDetailActivity.EXTRA_COMPLETED, task.isCompleted)
                            }
                            startActivity(intent)
                        },
                        onShareTask = { task ->
                            // --- IMPLICIT INTENT ---
                            // Declares ACTION_SEND without specifying a component, letting Android resolve share apps
                            val shareText = "Task: ${task.title}\nDescription: ${task.description}\nPriority: ${task.priority}\nDue: ${task.dueDate}"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Task via")
                            startActivity(shareIntent)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )

                    // Add / Edit Task Dialog with DatePickerDialog & Validation
                    if (isDialogVisible) {
                        AddEditTaskDialog(
                            task = taskToEdit,
                            onDismiss = {
                                isDialogVisible = false
                                taskToEdit = null
                            },
                            onSave = { title, description, priority, dueDate ->
                                val editing = taskToEdit
                                if (editing == null) {
                                    // Add new task
                                    val newTask = Task(
                                        id = (tasks.maxOfOrNull { it.id } ?: 0) + 1,
                                        title = title,
                                        description = description,
                                        priority = priority,
                                        dueDate = dueDate,
                                        isCompleted = false
                                    )
                                    tasks = listOf(newTask) + tasks
                                    Toast.makeText(this, "New task added", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Update existing task
                                    tasks = tasks.map { task ->
                                        if (task.id == editing.id) {
                                            task.copy(
                                                title = title,
                                                description = description,
                                                priority = priority,
                                                dueDate = dueDate
                                            )
                                        } else {
                                            task
                                        }
                                    }
                                    Toast.makeText(this, "Task edited", Toast.LENGTH_SHORT).show()
                                }
                                isDialogVisible = false
                                taskToEdit = null
                            }
                        )
                    }
                }
            }
        }
    }
}
