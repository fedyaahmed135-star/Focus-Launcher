package com.example.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data.TaskEntity
import com.example.ui.components.DynamicWallpaperSurface
import com.example.ui.components.MinimalistClockWidget
import com.example.ui.viewmodels.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WidgetsScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val taskViewModel: TaskViewModel = viewModel(factory = TaskViewModel.provideFactory(application))
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }

    DynamicWallpaperSurface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Real-time Minimalist Clock Widget
        MinimalistClockWidget(
            isLarge = false,
            showBattery = true,
            showSeconds = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Weather Widget
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.DarkMode,
                contentDescription = "Hava",
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "18°", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Color.White)
                Text(text = "Bakı\nAçıq", fontSize = 12.sp, color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Daily Plan Header with Add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Günün planı",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            IconButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Tapşırıq əlavə et",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dynamic Tasks List
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF141414))
                    .clickable { showAddDialog = true }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.AddCircleOutline,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Plan boşdur. Tapşırıq əlavə etmək üçün toxun.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(items = tasks, key = { it.id }) { task ->
                    DynamicPlanItem(
                        task = task,
                        onToggle = { taskViewModel.toggleTask(task) },
                        onDelete = { taskViewModel.deleteTask(task) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ToggleIcon(Icons.Filled.Wifi)
            ToggleIcon(Icons.Filled.Bluetooth)
            ToggleIcon(Icons.Filled.DarkMode)
            ToggleIcon(Icons.Filled.AirplanemodeActive)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Dock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockIcon(
                icon = Icons.Filled.Phone,
                contentDescription = "Zənglər",
                onClick = {
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            )
            DockIcon(
                icon = Icons.Filled.ChatBubble,
                contentDescription = "Mesajlar",
                onClick = {
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                            addCategory(android.content.Intent.CATEGORY_APP_MESSAGING)
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            )
            DockIcon(
                icon = Icons.Filled.Apps,
                contentDescription = "Tətbiqlər",
                onClick = { navController.navigate("app_drawer") }
            )
            DockIcon(
                icon = Icons.Filled.CameraAlt,
                contentDescription = "Kamera",
                onClick = {
                    try {
                        val intent = android.content.Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            )
        }
    }
}

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, time ->
                taskViewModel.addTask(title, time)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DynamicPlanItem(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 6.dp)
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,
                checkmarkColor = Color.Black,
                uncheckedColor = Color.Gray
            ),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                fontSize = 14.sp,
                color = if (task.isCompleted) Color.Gray else Color.White,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
            if (task.timeRange.isNotBlank()) {
                Text(
                    text = task.timeRange,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = "Sil",
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, time: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        title = {
            Text(
                text = "Yeni tapşırıq",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tapşırıq", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Vaxt (Məs: 10:00 - 11:30)", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, time)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Əlavə et", color = if (title.isNotBlank()) Color.White else Color.Gray)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ləğv et", color = Color.Gray)
            }
        }
    )
}

@Composable
fun ToggleIcon(icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF2C2C2C),
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

