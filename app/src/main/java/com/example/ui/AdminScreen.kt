package com.example.ui

import androidx.compose.animation.*
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JobPost

@Composable
fun AdminScreen(
    isAdminLoggedIn: Boolean,
    allJobs: List<JobPost>,
    onLogin: (String) -> Boolean,
    onLogout: () -> Unit,
    onAddJob: (String, String, String, String, String, String, String, String) -> Unit,
    onDeleteJob: (String) -> Unit,
    onResetDatabase: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isAdminLoggedIn) {
        AdminLoginGate(onLogin = onLogin, modifier = modifier)
    } else {
        AdminDashboard(
            allJobs = allJobs,
            onLogout = onLogout,
            onAddJob = onAddJob,
            onDeleteJob = onDeleteJob,
            onResetDatabase = onResetDatabase,
            modifier = modifier
        )
    }
}

@Composable
fun AdminLoginGate(
    onLogin: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .testTag("admin_login_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(4.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Lock Icon and Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Security lock",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Administrator Access",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Sign in to CareerHub console to manage job listings, purge profiles, and verify Mongo synchronization pathways.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Credentials hint
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Credential hint info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sandbox Authorization Key:  \"admin\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Password entry input
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        showError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input"),
                    label = { Text("Master Admin Password") },
                    placeholder = { Text("Enter sandbox password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Password shield")
                    },
                    isError = showError
                )

                if (showError) {
                    Text(
                        text = "Invalid credentials. Please re-check the key.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val success = onLogin(password)
                        if (!success) {
                            showError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_auth_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Authorize & Console In",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AdminDashboard(
    allJobs: List<JobPost>,
    onLogout: () -> Unit,
    onAddJob: (String, String, String, String, String, String, String, String) -> Unit,
    onDeleteJob: (String) -> Unit,
    onResetDatabase: () -> Unit,
    modifier: Modifier = Modifier
) {
    var screenTab by remember { mutableStateOf(0) } // 0 = Add New Job, 1 = Manage List
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Quick Header Actions
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "Console",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin Control Shell",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Internal Sandbox Engine",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onResetDatabase,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                .testTag("admin_reset_db_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset database listings",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                .testTag("admin_signout_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Log out",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tiny Stats summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DashboardStatBox("Sync Posts", "${allJobs.size}", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    DashboardStatBox("Applied Jobs", "${allJobs.count { it.isApplied }}", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                    DashboardStatBox("Saved Jobs", "${allJobs.count { it.isSaved }}", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                }
            }
        }

        // Sub navbar: Add vs Manage
        TabRow(
            selectedTabIndex = screenTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Tab(
                selected = (screenTab == 0),
                onClick = { screenTab = 0 },
                text = { Text("Publish Listing", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Publish", modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("admin_tab_add")
            )
            Tab(
                selected = (screenTab == 1),
                onClick = { screenTab = 1 },
                text = { Text("Manage Feed (${allJobs.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.List, contentDescription = "Manage list", modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("admin_tab_manage")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (screenTab == 0) {
            // Add New Job Listing Form
            JobPublishingForm(onAddJob = { t, c, l, s, y, cat, d, r ->
                onAddJob(t, c, l, s, y, cat, d, r)
                screenTab = 1 // Swap to manage view directly upon submit
            })
        } else {
            // Manage listings
            AdminJobList(allJobs = allJobs, onDeleteJob = onDeleteJob)
        }
    }
}

@Composable
fun DashboardStatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun JobPublishingForm(
    onAddJob: (String, String, String, String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Full-time") }
    var category by remember { mutableStateOf("Technology") }
    var description by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }

    val types = listOf("Full-time", "Part-time", "Remote", "Contract")
    val categories = listOf("Technology", "Design", "Marketing", "Business", "Healthcare")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
            .testTag("job_publishing_form"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Job Position Title") },
            placeholder = { Text("e.g. Senior ASP.NET Developer") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_admin_title"),
            singleLine = true
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Company Name") },
                placeholder = { Text("e.g. Stripe Syncs") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_admin_company"),
                singleLine = true
            )

            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = { Text("Salary Band") },
                placeholder = { Text("e.g. $120k - $140k") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_admin_salary"),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Office Location / Grid") },
            placeholder = { Text("e.g. Austin, TX (Hybrid)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_admin_location"),
            singleLine = true
        )

        // Employment Type chips selection
        Column {
            Text(
                text = "Employment Structure Choice",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                types.forEach { typeOption ->
                    val selected = (type == typeOption)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        border = BorderStroke(1.dp, if (selected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { type = typeOption }
                            .testTag("admin_type_$typeOption")
                    ) {
                        Text(
                            text = typeOption,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Category Selection Row slider
        Column {
            Text(
                text = "Industry Category Segment",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(categories) { categoryOption ->
                    val selected = (category == categoryOption)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
                        contentColor = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        border = BorderStroke(1.dp, if (selected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .clickable { category = categoryOption }
                            .testTag("admin_cat_$categoryOption")
                    ) {
                        Text(
                            text = categoryOption,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Role Description Overview") },
            placeholder = { Text("Provide details about the role, technical architectures, and developer ecosystems.") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("input_admin_description"),
            maxLines = 4
        )

        OutlinedTextField(
            value = requirements,
            onValueChange = { requirements = it },
            label = { Text("Listing Requirements (line per point)") },
            placeholder = { Text("• 3+ years experience in C#\n• Strong MongoDB queries modeling\n• Modern Android integrations") },
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .testTag("input_admin_requirements"),
            maxLines = 5
        )

        Button(
            onClick = {
                if (title.isNotEmpty() && company.isNotEmpty()) {
                    onAddJob(
                        title, company, location, salary, type, category,
                        description,
                        if (requirements.isEmpty()) "• Experience with modern cloud frameworks" else requirements
                    )
                }
            },
            enabled = (title.isNotEmpty() && company.isNotEmpty()),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .height(48.dp)
                .testTag("admin_publish_submit_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Inject & Publish Posting Live",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AdminJobList(
    allJobs: List<JobPost>,
    onDeleteJob: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (allJobs.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "No jobs icon",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No job postings compiled. Enter listings or click Reset to default DB configuration.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(allJobs, key = { it.id }) { job ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_job_row_${job.id}"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = job.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = job.company,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• ${job.category}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Pruning button action
                        IconButton(
                            onClick = { onDeleteJob(job.id) },
                            modifier = Modifier.size(36.dp).testTag("admin_delete_btn_${job.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove job listing",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
