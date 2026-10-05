package com.ozerli.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ozerli.app.data.AppDatabase
import com.ozerli.app.data.Task
import com.ozerli.app.data.Urgency
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).taskDao()

    val openTasks = dao.getOpenTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val doneTasks = dao.getDoneTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val openCount = dao.getOpenCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addTask(title: String, description: String, priority: Urgency, dueDate: Long?) {
        viewModelScope.launch {
            dao.insert(
                Task(
                    title = title.trim(),
                    description = description.trim(),
                    priority = priority,
                    dueDate = dueDate
                )
            )
        }
    }

    fun markDone(task: Task) {
        viewModelScope.launch {
            dao.update(task.copy(isDone = true, doneAt = System.currentTimeMillis()))
        }
    }

    fun reopenTask(task: Task) {
        viewModelScope.launch {
            dao.update(task.copy(isDone = false, doneAt = null))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { dao.delete(task) }
    }

    fun updatePriority(task: Task, priority: Urgency) {
        viewModelScope.launch { dao.update(task.copy(priority = priority)) }
    }
}
