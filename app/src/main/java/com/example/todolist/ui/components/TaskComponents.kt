
package com.example.todolist.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.model.Task
import java.util.Calendar
import java.time.LocalDate

@Composable
fun TaskListContent(
    tasks: List<Task>,
    onToggleCompletion: (Int) -> Unit,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onTaskClick: (Task) -> Unit,
    onShareTask: (Task) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf("All", "Active", "Completed")
    val filteredTasks = when (selectedFilter) {
        "Active" -> tasks.filter { !it.isCompleted }
        "Completed" -> tasks.filter { it.isCompleted }
        else -> tasks
    }
    if (tasks.isEmpty()) {
        EmptyTaskState(modifier = modifier)
    } else {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterChange(filter) },
                        label = { Text(filter) }
                    )
                }
            }
            if (filteredTasks.isEmpty()) {//3lshan lw mafesh tasks fe elfilter elly ekhtarto
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks in this filter.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->

                        SwipeToDeleteTaskItem(
                            onDelete = { onDeleteTask(task.id) }//3lsshan amsh mn eldatabase lma y3mel swipe
                        ) {
                            TaskItemCard(
                                task = task,
                                onToggleCompletion = {
                                    onToggleCompletion(task.id)
                                },
                                onEdit = { onEditTask(task) },
                                onDelete = { onDeleteTask(task.id) },
                                onClick = { onTaskClick(task) },
                                onShare = { onShareTask(task) }
                            )
                        }
                    }
                }
            }
        }
    }
}@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteTaskItem(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {//endToStart y3ny bswipe mn right To left
                onDelete()
                true //wafe't letaghyer halet elstatre le dismiss
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,//bmn3 eldismiss mn elLeft To Right
        enableDismissFromEndToStart = true,
        backgroundContent = {//elly byzhar wara lma aswipe elcard
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = Color.White
                )
            }
        },
        content = {
            content()
        }
    )
}

@Composable
fun EmptyTaskState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No tasks available. Tap + to add one!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
fun TaskItemCard(
    task: Task,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (task.priority) {
        "High" -> MaterialTheme.colorScheme.error
        "Low" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.tertiary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskCheckbox(
                isCompleted = task.isCompleted,
                onCheckedChange = { onToggleCompletion() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskTitle(
                        title = task.title,
                        isCompleted = task.isCompleted,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    TaskPriority(priority = task.priority, color = priorityColor)
                }

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TaskDescription(description = task.description)
                }

                if (task.dueDate.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    TaskDate(dueDate = task.dueDate)
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            //action buttons (Share, Edit, Delete)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share Task",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Task",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCheckbox(
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Checkbox(
        checked = isCompleted,
        onCheckedChange = onCheckedChange,
        modifier = modifier
    )
}

@Composable
fun TaskTitle(
    title: String,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun TaskDescription(
    description: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun TaskPriority(
    priority: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = priority,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun TaskDate(
    dueDate: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Due Date",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "Due: $dueDate",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun AddEditTaskDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, priority: String, dueDate: String) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: "Medium") }
    var dueDate by remember {
        mutableStateOf(task?.dueDate ?: LocalDate.now().plusDays(1).toString())
    }
    var titleError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dueDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Add New Task" else "Edit Task") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Task Title *") },
                    isError = titleError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (titleError) {
                    Text(
                        text = "Title cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // select priority level using FilterChips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Priority Level",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Low", "Medium", "High").forEach { p ->
                            FilterChip(
                                selected = priority == p,
                                onClick = { priority = p },
                                label = { Text(p) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Due Date Picker Field
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Due Date") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Due Date")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        onSave(title, description, priority, dueDate)
                    }
                }
            ) {
                Text(if (task == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}