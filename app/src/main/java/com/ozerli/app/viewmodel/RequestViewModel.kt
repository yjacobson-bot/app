package com.ozerli.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ozerli.app.data.AppDatabase
import com.ozerli.app.data.Request
import com.ozerli.app.data.Urgency
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RequestViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).requestDao()

    val openRequests = dao.getOpenRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val doneRequests = dao.getDoneRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val openCount = dao.getOpenCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val doneCount = dao.getDoneCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val topRequesters = dao.getTopRequesters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRequest(
        personName: String,
        shiur: String,
        description: String,
        urgency: Urgency,
        reminderAt: Long?
    ) {
        viewModelScope.launch {
            dao.insert(
                Request(
                    personName = personName.trim(),
                    shiur = shiur.trim(),
                    description = description.trim(),
                    urgency = urgency,
                    reminderAt = reminderAt
                )
            )
        }
    }

    fun markDone(request: Request, note: String) {
        viewModelScope.launch {
            dao.update(
                request.copy(
                    isDone = true,
                    doneNote = note.trim(),
                    doneAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun reopenRequest(request: Request) {
        viewModelScope.launch {
            dao.update(
                request.copy(
                    isDone = false,
                    doneNote = "",
                    doneAt = null
                )
            )
        }
    }

    fun deleteRequest(request: Request) {
        viewModelScope.launch {
            dao.delete(request)
        }
    }

    fun updateUrgency(request: Request, urgency: Urgency) {
        viewModelScope.launch {
            dao.update(request.copy(urgency = urgency))
        }
    }
}
