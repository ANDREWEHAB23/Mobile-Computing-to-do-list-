
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.data.settings.SortOrder
import com.example.todolist.model.Task
import com.example.todolist.ui.components.AddEditTaskDialog
import com.example.todolist.ui.components.TaskListContent
import com.example.todolist.ui.settings.SettingsViewModel
import com.example.todolist.ui.theme.ToDoListTheme
import com.example.todolist.util.ResponsiveContainer
import com.example.todolist.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            val taskViewModel: TaskViewModel = viewModel()
            val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()
            ToDoListTheme(darkTheme = settings.darkMode) {
                var isDialogVisible by remember { mutableStateOf(false) }
                var selectedFilter by remember { mutableStateOf("All") }
                var taskToEdit by remember { mutableStateOf<Task?>(null) }
                val sortedTasks = when (settings.sortOrder) {
                    SortOrder.DATE ->
                        tasks.sortedBy { it.dueDate }
                    SortOrder.PRIORITY ->
                        tasks.sortedBy {
                            when (it.priority) {
                                "High" -> 0
                                "Medium" -> 1
                                else -> 2
                            }
                        }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    "To-Do List",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        startActivity(
                                            Intent(
                                                this@MainActivity,
                                                SettingsActivity::class.java
                                            )
                                        )
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = "Settings"
                                    )
                                }
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
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Task"
                            )
                        }
                    }

                ) { innerPadding ->
                    ResponsiveContainer( // 3lshan yeb'a Responsive layout le different screen sizes
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        TaskListContent(
                            tasks = sortedTasks,

                            selectedFilter = selectedFilter,

                            onFilterChange = {
                                selectedFilter = it
                            },

                            onToggleCompletion = { taskId ->

                                val task = tasks.find {
                                    it.id == taskId
                                }

                                if (task != null) {
                                    taskViewModel.toggleTaskCompletion(task)

                                    Toast.makeText(
                                        this@MainActivity,
                                        "Task updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },

                            onEditTask = { task ->
                                taskToEdit = task
                                isDialogVisible = true
                            },

                            onDeleteTask = { taskId ->

                                val task = tasks.find {
                                    it.id == taskId
                                }

                                if (task != null) {
                                    taskViewModel.deleteTask(task)

                                    Toast.makeText(
                                        this@MainActivity,
                                        "Task deleted",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },

                            onTaskClick = { task ->

                                val intent = Intent(
                                    this@MainActivity,
                                    TaskDetailActivity::class.java
                                ).apply {
                                    putExtra(
                                        TaskDetailActivity.EXTRA_TITLE,
                                        task.title
                                    )

                                    putExtra(
                                        TaskDetailActivity.EXTRA_DESCRIPTION,
                                        task.description
                                    )

                                    putExtra(
                                        TaskDetailActivity.EXTRA_PRIORITY,
                                        task.priority
                                    )

                                    putExtra(
                                        TaskDetailActivity.EXTRA_DUE_DATE,
                                        task.dueDate
                                    )

                                    putExtra(
                                        TaskDetailActivity.EXTRA_COMPLETED,
                                        task.isCompleted
                                    )
                                }

                                startActivity(intent)
                            },
                            onShareTask = { task ->
                                val shareText =
                                    "Task: ${task.title}\n" +
                                            "Description: ${task.description}\n" +
                                            "Priority: ${task.priority}\n" +
                                            "Due: ${task.dueDate}"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        shareText
                                    )
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(
                                    sendIntent,
                                    "Share Task via"
                                )
                                startActivity(shareIntent)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
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
                                    val newTask = Task(
                                        id = 0,
                                        title = title,
                                        description = description,
                                        priority = priority,
                                        dueDate = dueDate,
                                        isCompleted = false
                                    )
                                    taskViewModel.addTask(newTask)
                                } else {
                                    taskViewModel.updateTask(
                                        editing.copy(
                                            title = title,
                                            description = description,
                                            priority = priority,
                                            dueDate = dueDate
                                        )
                                    )
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
