package com.example.familytasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.familytasks.data.FamilyDatabase
import com.example.familytasks.data.entity.ColorPreferenceEntity
import com.example.familytasks.data.entity.FamilyUserEntity
import com.example.familytasks.data.entity.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class FamilyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FamilyDatabase.getInstance(application)
    private val userDao = db.userDao()
    private val taskDao = db.taskDao()
    private val colorPreferenceDao = db.colorPreferenceDao()

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    val users = userDao.observeAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val currentUser = currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else userDao.observeById(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val tasks = currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else taskDao.observeForOwner(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val currentColorPreference = currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else colorPreferenceDao.observeByUserId(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            ensureSeedData()
        }
    }

    private suspend fun ensureSeedData() {
        if (userDao.getAllOnce().isEmpty()) {
            userDao.upsertAll(defaultUsers())
        }
        if (colorPreferenceDao.getAllOnce().isEmpty()) {
            colorPreferenceDao.upsertAll(defaultColors())
        }
    }

    private fun defaultUsers(): List<FamilyUserEntity> = listOf(
        FamilyUserEntity(id = "son", displayName = "Son", tapCode = 3),
        FamilyUserEntity(id = "wife", displayName = "Wife", tapCode = 4),
        FamilyUserEntity(id = "dad", displayName = "Dad", tapCode = 5)
    )

    private fun defaultColors(): List<ColorPreferenceEntity> = listOf(
        ColorPreferenceEntity(
            userId = "son",
            brightHex = "#FF6B6B",
            mutedHex = "#7A1D1D"
        ),
        ColorPreferenceEntity(
            userId = "wife",
            brightHex = "#C77DFF",
            mutedHex = "#4E2671"
        ),
        ColorPreferenceEntity(
            userId = "dad",
            brightHex = "#2A2A2A",
            mutedHex = "#000000"
        )
    )

    fun authenticateWithTapCode(tapCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getByTapCode(tapCode)
            _currentUserId.value = user?.id
        }
    }

    fun saveUserNames(sonName: String, wifeName: String, dadName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.upsert(FamilyUserEntity(id = "son", displayName = sonName.ifBlank { "Son" }, tapCode = 3))
            userDao.upsert(FamilyUserEntity(id = "wife", displayName = wifeName.ifBlank { "Wife" }, tapCode = 4))
            userDao.upsert(FamilyUserEntity(id = "dad", displayName = dadName.ifBlank { "Dad" }, tapCode = 5))
        }
    }

    fun addTask(task: String, details: String) {
        val ownerId = currentUserId.value ?: return
        val cleanTask = task.trim()
        if (cleanTask.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            taskDao.insert(
                TaskEntity(
                    task = cleanTask,
                    details = details.trim(),
                    owner = ownerId
                )
            )
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.delete(task)
        }
    }

    fun logout() {
        _currentUserId.value = null
    }
}
