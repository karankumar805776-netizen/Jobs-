package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.JobPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobPortalApp(
    viewModel: JobViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val filteredJobs by viewModel.filteredJobs.collectAsStateWithLifecycle()
    val appliedJobs by viewModel.appliedJobs.collectAsStateWithLifecycle()
    val savedJobs by viewModel.savedJobs.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isEditingProfile by viewModel.isEditingProfile.collectAsStateWithLifecycle()
    val selectedJob by viewModel.selectedJob.collectAsStateWithLifecycle()
    val allJobs by viewModel.allJobs.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()

    var showAdminMode by remember { mutableStateOf(false) }
    var showDbArchModal by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("app_scaffold"),
        topBar = {
            Column {
                // Top Custom Modern Bar
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (showAdminMode) "CareerHub Console" else "CareerHub",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                color = if (showAdminMode) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = (if (showAdminMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (showAdminMode) "ADMIN" else "v1.0",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showAdminMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        // DB Connection Info Shortcut
                        IconButton(
                            onClick = { showDbArchModal = true },
                            modifier = Modifier.testTag("db_status_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Database architecture status info",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Admin Mode Gate Lock Button
                        IconButton(
                            onClick = { showAdminMode = !showAdminMode },
                            modifier = Modifier.testTag("toggle_admin_mode")
                        ) {
                            Icon(
                                imageVector = if (showAdminMode) Icons.Default.Close else Icons.Default.Lock,
                                contentDescription = "Toggle admin mode",
                                tint = if (showAdminMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )

                // Search Bar shown on non-profile screens
                if (currentTab != JobViewModel.Tab.PROFILE && !showAdminMode) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Search jobs, companies, requirements...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("search_field"),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        },
        bottomBar = {
            if (!showAdminMode) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = (currentTab == JobViewModel.Tab.HOME),
                        onClick = { viewModel.selectTab(JobViewModel.Tab.HOME) },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home tab") },
                        label = { Text("Explore") },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = (currentTab == JobViewModel.Tab.APPLIED),
                        onClick = { viewModel.selectTab(JobViewModel.Tab.APPLIED) },
                        icon = { Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Applied tab") },
                        label = { Text("Applied") },
                        modifier = Modifier.testTag("nav_applied")
                    )
                    NavigationBarItem(
                        selected = (currentTab == JobViewModel.Tab.SAVED),
                        onClick = { viewModel.selectTab(JobViewModel.Tab.SAVED) },
                        icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Saved tab") },
                        label = { Text("Saved") },
                        modifier = Modifier.testTag("nav_saved")
                    )
                    NavigationBarItem(
                        selected = (currentTab == JobViewModel.Tab.PROFILE),
                        onClick = { viewModel.selectTab(JobViewModel.Tab.PROFILE) },
                        icon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile tab") },
                        label = { Text("Resume") },
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (showAdminMode) {
                AdminScreen(
                    isAdminLoggedIn = isAdminLoggedIn,
                    allJobs = allJobs,
                    onLogin = { viewModel.loginAsAdmin(it) },
                    onLogout = {
                        viewModel.logoutAdmin()
                        showAdminMode = false
                    },
                    onAddJob = { t, c, l, s, y, cat, d, r ->
                        viewModel.addJobByAdmin(t, c, l, s, y, cat, d, r)
                    },
                    onDeleteJob = { viewModel.deleteJobByAdmin(it) },
                    onResetDatabase = { viewModel.resetDatabaseByAdmin() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                when (currentTab) {
                    JobViewModel.Tab.HOME -> {
                        ExploreSection(
                            selectedCategory = selectedCategory,
                            filteredJobs = filteredJobs,
                            onCategorySelect = { viewModel.onCategoryChange(it) },
                            onViewDetails = { viewModel.viewJobDetails(it) },
                            onToggleSave = { viewModel.toggleSaveJob(it) }
                        )
                    }
                    JobViewModel.Tab.APPLIED -> {
                        AppliedSection(
                            appliedJobs = appliedJobs,
                            onViewDetails = { viewModel.viewJobDetails(it) },
                            onToggleSave = { viewModel.toggleSaveJob(it) }
                        )
                    }
                    JobViewModel.Tab.SAVED -> {
                        SavedSection(
                            savedJobs = savedJobs,
                            onViewDetails = { viewModel.viewJobDetails(it) },
                            onToggleSave = { viewModel.toggleSaveJob(it) }
                        )
                    }
                    JobViewModel.Tab.PROFILE -> {
                        ProfileScreen(
                            profile = userProfile,
                            isEditing = isEditingProfile,
                            onStartEdit = { viewModel.startEditingProfile() },
                            onCancelEdit = { viewModel.stopEditingProfile() },
                            onSaveProfile = { viewModel.saveUserProfile(it) }
                        )
                    }
                }
            }

            // Database Arch Modal trigger
            if (showDbArchModal) {
                Dialog(
                    onDismissRequest = { showDbArchModal = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .wrapContentHeight()
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "System Architecture",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(onClick = { showDbArchModal = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Close dialog"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            DbArchitectureSection()
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showDbArchModal = false },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Got it")
                            }
                        }
                    }
                }
            }

            // Full Job Detail Screen Overlay Dialog
            selectedJob?.let { job ->
                Dialog(
                    onDismissRequest = { viewModel.viewJobDetails(null) },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .fillMaxHeight(0.9f),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.background,
                        tonalElevation = 6.dp
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Header bar of Detail Overlay
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Job Details",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                IconButton(
                                    onClick = { viewModel.viewJobDetails(null) },
                                    modifier = Modifier.testTag("detail_close")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close detail panel"
                                    )
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // Detail Scroll View
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = job.title,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 28.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = job.company,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Tags row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Badge(
                                        text = job.type,
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Badge(
                                        text = job.location,
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Badge(
                                        text = job.salary,
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Overview",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = job.description,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Requirements",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                job.requirements.split("\n").forEach { bullet ->
                                    Text(
                                        text = bullet,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))

                                // Embed design architecture context explaining Mongo and C# link
                                DbArchitectureSection()
                            }

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // Bottom actions inside sheet
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { viewModel.toggleSaveJob(job) },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                            .testTag("detail_save_btn")
                                    ) {
                                        Icon(
                                            imageVector = if (job.isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                            contentDescription = "Save job",
                                            tint = if (job.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.applyToJob(job) },
                                        enabled = !job.isApplied,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("detail_apply_btn"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (job.isApplied) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        if (job.isApplied) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Submitted",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Applied - Under Review", fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Text("Apply Now", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreSection(
    selectedCategory: String,
    filteredJobs: List<JobPost>,
    onCategorySelect: (String) -> Unit,
    onViewDetails: (JobPost) -> Unit,
    onToggleSave: (JobPost) -> Unit
) {
    val categories = listOf("All", "Technology", "Design", "Marketing", "Business", "Healthcare")

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Statistics Bar to enrich visual layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatisticCard(
                title = "Total Jobs",
                value = "7 Active",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send list",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "C# / Mongo",
                value = "2 Backend",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Database gear",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }

        // Horizontal Category slider Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("category_slider"),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    isSelected = (category == selectedCategory),
                    onClick = { onCategorySelect(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Job Feed List
        if (filteredJobs.isEmpty()) {
            EmptyListPlaceholder("No jobs match your active filters or query.")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("jobs_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredJobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        onViewDetails = { onViewDetails(job) },
                        onToggleSave = { onToggleSave(job) }
                    )
                }
            }
        }
    }
}

@Composable
fun AppliedSection(
    appliedJobs: List<JobPost>,
    onViewDetails: (JobPost) -> Unit,
    onToggleSave: (JobPost) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Tracked Submissions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )

        if (appliedJobs.isEmpty()) {
            EmptyListPlaceholder(
                message = "You haven't applied to any roles yet.\nExplore the feed to send your first resume!"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("applied_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(appliedJobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        onViewDetails = { onViewDetails(job) },
                        onToggleSave = { onToggleSave(job) }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedSection(
    savedJobs: List<JobPost>,
    onViewDetails: (JobPost) -> Unit,
    onToggleSave: (JobPost) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Bookmarked Career Paths",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )

        if (savedJobs.isEmpty()) {
            EmptyListPlaceholder(
                message = "No saved positions. Tap the heart outline next to any job descriptions to save them!"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("saved_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedJobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        onViewDetails = { onViewDetails(job) },
                        onToggleSave = { onToggleSave(job) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyListPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("empty_list_holder"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Empty list icon",
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
