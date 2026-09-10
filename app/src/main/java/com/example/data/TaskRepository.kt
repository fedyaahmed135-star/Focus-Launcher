package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun addTask(title: String, timeRange: String) {
        taskDao.insertTask(
            TaskEntity(
                title = title,
                timeRange = timeRange,
                isCompleted = false
            )
        )
    }

    suspend fun toggleTask(task: TaskEntity) {
        taskDao.updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }
}
