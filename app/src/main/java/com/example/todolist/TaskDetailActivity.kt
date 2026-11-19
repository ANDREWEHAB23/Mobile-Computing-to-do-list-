package com.example.todolist

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todolist.ui.theme.ToDoListTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.ui.settings.SettingsViewModel
class TaskDetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_PRIORITY = "extra_priority"
        const val EXTRA_DUE_DATE = "extra_due_date"
        const val EXTRA_COMPLETED = "extra_completed"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- EXPLICIT INTENT: receive data passed from MainActivity ---
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "No Title"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "No Description"
        val priority = intent.getStringExtra(EXTRA_PRIORITY) ?: "Medium"
        val dueDate = intent.getStringExtra(EXTRA_DUE_DATE) ?: "No Due Date"
        val isCompleted = intent.getBooleanExtra(EXTRA_COMPLETED, false)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

            ToDoListTheme(darkTheme = settings.darkMode) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Task Details") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    HorizontalDivider()

                                    Text(
                                        text = "Description:",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "Priority", style = MaterialTheme.typography.labelMedium)
                                            Text(text = priority, fontWeight = FontWeight.SemiBold)
                                        }
                                        Column {
                                            Text(text = "Due Date", style = MaterialTheme.typography.labelMedium)
                                            Text(text = dueDate, fontWeight = FontWeight.SemiBold)
                                        }
                                        Column {
                                            Text(text = "Status", style = MaterialTheme.typography.labelMedium)
                                            Text(
                                                text = if (isCompleted) "Completed" else "Active",
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // --- IMPLICIT INTENT: Share task details ---
                            Button(
                                onClick = {
                                    val shareText = "Task: $title\nDescription: $description\nPriority: $priority\nDue: $dueDate"
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Share Task via")
                                    startActivity(shareIntent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share Task")
                            }
                        }
                    }
                }
            }
        }
    }
}
