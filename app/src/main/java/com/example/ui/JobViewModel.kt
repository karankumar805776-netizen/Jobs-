package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.JobPost
import com.example.data.JobRepository
import com.example.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JobViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = JobRepository(database.jobDao(), database.profileDao())

    init {
        // Pre-populate database with default jobs and profile if empty
        viewModelScope.launch {
            repository.checkAndPrepopulateDb()
        }
    }

    // Search and filters State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Screen selection (internal navigation tab state)
    // TAB enum: "Home", "Applied", "Saved", "Profile"
    private val _currentTab = MutableStateFlow(Tab.HOME)
    val currentTab: StateFlow<Tab> = _currentTab.asStateFlow()

    // Selected job for detailed bottom sheet or full-detail screen
    private val _selectedJob = MutableStateFlow<JobPost?>(null)
    val selectedJob: StateFlow<JobPost?> = _selectedJob.asStateFlow()

    // Profile editing state
    private val _isEditingProfile = MutableStateFlow(false)
    val isEditingProfile: StateFlow<Boolean> = _isEditingProfile.asStateFlow()

    // Admin state
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    enum class Tab {
        HOME, APPLIED, SAVED, PROFILE
    }

    // Expose User Profile State
    val allJobs: StateFlow<List<JobPost>> = repository.allJobs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Filter jobs reactively based on search, category and current tab (e.g. only saved)
    val filteredJobs: StateFlow<List<JobPost>> = combine(
        repository.allJobs,
        _searchQuery,
        _selectedCategory
    ) { jobsList, query, cat ->
        jobsList.filter { job ->
            val matchesCategory = if (cat == "All") true else job.category.equals(cat, ignoreCase = true)
            val matchesQuery = if (query.isEmpty()) true else {
                job.title.contains(query, ignoreCase = true) ||
                job.company.contains(query, ignoreCase = true) ||
                job.description.contains(query, ignoreCase = true) ||
                job.requirements.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Applied jobs list
    val appliedJobs: StateFlow<List<JobPost>> = repository.allJobs
        .combine(_searchQuery) { jobsList, query ->
            jobsList.filter { it.isApplied }.filter { job ->
                if (query.isEmpty()) true else {
                    job.title.contains(query, ignoreCase = true) || job.company.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Saved jobs list
    val savedJobs: StateFlow<List<JobPost>> = repository.allJobs
        .combine(_searchQuery) { jobsList, query ->
            jobsList.filter { it.isSaved }.filter { job ->
                if (query.isEmpty()) true else {
                    job.title.contains(query, ignoreCase = true) || job.company.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryChange(category: String) {
        _selectedCategory.value = category
    }

    fun selectTab(tab: Tab) {
        _currentTab.value = tab
        // Clear search queries when swapping major lists for visual sanity
        _searchQuery.value = ""
    }

    fun viewJobDetails(job: JobPost?) {
        _selectedJob.value = job
    }

    fun toggleSaveJob(job: JobPost) {
        viewModelScope.launch {
            repository.updateSaveStatus(job.id, !job.isSaved)
            // Update the selected job details if currently visible
            if (_selectedJob.value?.id == job.id) {
                _selectedJob.value = _selectedJob.value?.copy(isSaved = !job.isSaved)
            }
        }
    }

    fun applyToJob(job: JobPost) {
        viewModelScope.launch {
            repository.updateApplicationStatus(
                jobId = job.id,
                isApplied = true,
                status = "Applied",
                appliedDate = System.currentTimeMillis()
            )
            // Update the selected job details if currently visible
            if (_selectedJob.value?.id == job.id) {
                _selectedJob.value = _selectedJob.value?.copy(
                    isApplied = true,
                    applicationStatus = "Applied",
                    appliedDate = System.currentTimeMillis()
                )
            }
        }
    }

    fun startEditingProfile() {
        _isEditingProfile.value = true
    }

    fun stopEditingProfile() {
        _isEditingProfile.value = false
    }

    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateProfile(profile)
            _isEditingProfile.value = false
        }
    }

    fun loginAsAdmin(password: String): Boolean {
        return if (password.trim() == "admin") {
            _isAdminLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    fun addJobByAdmin(
        title: String,
        company: String,
        location: String,
        salary: String,
        type: String,
        category: String,
        description: String,
        requirements: String
    ) {
        viewModelScope.launch {
            val newJob = JobPost(
                id = java.util.UUID.randomUUID().toString(),
                title = title,
                company = company,
                location = location,
                salary = salary,
                type = type,
                category = category,
                description = description,
                requirements = requirements,
                postedTime = "Just now"
            )
            repository.addJob(newJob)
        }
    }

    fun deleteJobByAdmin(jobId: String) {
        viewModelScope.launch {
            repository.deleteJob(jobId)
            // If the deleted job is currently selected, clear it
            if (_selectedJob.value?.id == jobId) {
                _selectedJob.value = null
            }
        }
    }

    fun resetDatabaseByAdmin() {
        viewModelScope.launch {
            repository.resetDatabase()
            _selectedJob.value = null
        }
    }
}

class JobViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JobViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JobViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
