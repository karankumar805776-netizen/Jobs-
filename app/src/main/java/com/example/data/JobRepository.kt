package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class JobRepository(
    private val jobDao: JobDao,
    private val profileDao: ProfileDao
) {
    val allJobs: Flow<List<JobPost>> = jobDao.getAllJobsFlow()
    val userProfile: Flow<UserProfile?> = profileDao.getProfileFlow()

    fun getJobByIdFlow(id: String): Flow<JobPost?> = jobDao.getJobByIdFlow(id)

    suspend fun updateSaveStatus(jobId: String, isSaved: Boolean) {
        withContext(Dispatchers.IO) {
            jobDao.updateSaveStatus(jobId, isSaved)
        }
    }

    suspend fun updateApplicationStatus(jobId: String, isApplied: Boolean, status: String, appliedDate: Long) {
        withContext(Dispatchers.IO) {
            jobDao.updateApplicationStatus(jobId, isApplied, status, appliedDate)
        }
    }

    suspend fun updateProfile(profile: UserProfile) {
        withContext(Dispatchers.IO) {
            profileDao.updateProfile(profile)
        }
    }

    suspend fun addJob(job: JobPost) {
        withContext(Dispatchers.IO) {
            jobDao.insertSingleJob(job)
        }
    }

    suspend fun deleteJob(jobId: String) {
        withContext(Dispatchers.IO) {
            jobDao.deleteJobById(jobId)
        }
    }

    suspend fun resetDatabase() {
        withContext(Dispatchers.IO) {
            jobDao.deleteAllJobs()
            // Immediately run populate check to reload default lists
            val defaultJobs = listOf(
                JobPost(
                    id = "1",
                    title = "Full-stack C# .NET Developer (MongoDB)",
                    company = "InnovateHQ Ltd",
                    location = "Seattle, WA (Remote)",
                    salary = "$115k - $135k",
                    type = "Remote",
                    category = "Technology",
                    description = "We are seeking a Full-Stack C# .NET Developer with strong MongoDB experience. You will design, build, and maintain robust API servers using ASP.NET Core, integration layers with non-relational database structures, and help integrate companion mobile platforms.",
                    requirements = "• 3+ years experience with C# .NET Core.\n• Strong databases skills: MongoDB (NoSQL) & document modeling.\n• Solid understanding of RESTful API design.\n• Experience with containerization (Docker, Kubernetes).\n• Nice to have: Knowledge of Android/iOS app API integrations.",
                    postedTime = "2 hours ago"
                ),
                JobPost(
                    id = "2",
                    title = "UI/UX Product Designer",
                    company = "CreativeFlow Agency",
                    location = "New York, NY",
                    salary = "$95k - $110k",
                    type = "Full-time",
                    category = "Design",
                    description = "CreativeFlow is looking for a UI/UX Designer to craft premium, modern, high-contrast visual designs for our portfolio clients. You will build interface designs, layouts, visual prototypes, and work closely with Android/Web engineering groups.",
                    requirements = "• Robust portfolio showcasing mobile and desktop interface layouts.\n• Advanced proficiency in Figma and design tools.\n• Strong grasp of Material 3 and adaptive layout principles.\n• Excellent communication skills and micro-interaction planning.",
                    postedTime = "1 day ago"
                ),
                JobPost(
                    id = "3",
                    title = "Senior .NET Enterprise Architect",
                    company = "Enterprise Cloud Corp",
                    location = "Austin, TX (Hybrid)",
                    salary = "$140k - $165k",
                    type = "Full-time",
                    category = "Technology",
                    description = "Enterprise Cloud Corp is scaling its mission-critical infrastructure. We need a seasoned Architect/Engineer to design high-throughput distributed architectures on the Microsoft web platform, leveraging MongoDB and Redis caches.",
                    requirements = "• 7+ years of expertise in C# and advanced .NET framework.\n• Deep knowledge of distributed systems, CQRS, and Event-Driven architecture.\n• Extensive practical integration of MongoDB collections and indexing.\n• Familiarity with Azure and public cloud architectures.",
                    postedTime = "3 days ago"
                ),
                JobPost(
                    id = "4",
                    title = "Growth Marketing Manager",
                    company = "BrandAmplify",
                    location = "Los Angeles, CA",
                    salary = "$80k - $95k",
                    type = "Contract",
                    category = "Marketing",
                    description = "BrandAmplify is hiring a creative, data-driven Growth Marketer to lead campaign strategies and scaling models. You will design user aquisition experiments, track metrics, and run content engines across digital visual feeds.",
                    requirements = "• 4+ years running B2C or App Growth funnels.\n• Certified analytics tracking (GA4, Mixpanel, Amplitude).\n• Proven budget scaling and customer acquisition cost (CAC) optimization.\n• Energetic, creative visual layout planner.",
                    postedTime = "4 days ago"
                ),
                JobPost(
                    id = "5",
                    title = "Senior iOS & Android Engineer",
                    company = "AppMasters Co",
                    location = "San Francisco, CA (Remote)",
                    salary = "$135k - $155k",
                    type = "Remote",
                    category = "Technology",
                    description = "AppMasters is seeking a Mobile developer to build modern client apps in Jetpack Compose and native Kotlin. You'll sync app data perfectly with background REST endpoints and local SQLite Room database layers.",
                    requirements = "• 4+ years of native mobile development.\n• Heavy Compose UI and offline-first design (Room/SQLite).\n• Experience with modern reactive patterns (StateFlow, Coroutines).\n• Excellent eye for UI motion, spacing, and transition styling.",
                    postedTime = "5 days ago"
                ),
                JobPost(
                    id = "6",
                    title = "Lead pediatric Registered Nurse (RN)",
                    company = "St. Jude Care Center",
                    location = "Denver, CO",
                    salary = "$85k - $100k",
                    type = "Full-time",
                    category = "Healthcare",
                    description = "St. Jude Care Center has an opening for a highly dedicated Lead Pediatric Nurse. You will manage outpatient clinical visits, design health care schedules, and lead a stellar group of clinical practitioners.",
                    requirements = "• Active Registered Nurse (RN) License in CO.\n• 3+ years experience in pediatrics or family medicine.\n• BLS (Basic Life Support) and PALS certification.\n• Compassionate client care style.",
                    postedTime = "1 week ago"
                ),
                JobPost(
                    id = "7",
                    title = "Business Analyst / Scrum Master",
                    company = "Capital Growth Partners",
                    location = "Chicago, IL",
                    salary = "$90k - $110k",
                    type = "Full-time",
                    category = "Business",
                    description = "Join Capital Growth as a versatile Scrum Master and Financial Business Analyst. You will translate stakeholder user requirements into developmental epics and design visual analytics reports.",
                    requirements = "• Certified Scrum Master (CSM) or equivalent.\n• Strong financial indexing and SQL querying skills.\n• Ability to run sprint plan sessions across multi-lingual teams.\n• Experience with JIRA and product dashboarding assets.",
                    postedTime = "1 week ago"
                )
            )
            jobDao.insertJobs(defaultJobs)
        }
    }

    suspend fun checkAndPrepopulateDb() {
        withContext(Dispatchers.IO) {
            val jobCount = jobDao.getCount()
            if (jobCount == 0) {
                // Populate jobs
                val defaultJobs = listOf(
                    JobPost(
                        id = "1",
                        title = "Full-stack C# .NET Developer (MongoDB)",
                        company = "InnovateHQ Ltd",
                        location = "Seattle, WA (Remote)",
                        salary = "$115k - $135k",
                        type = "Remote",
                        category = "Technology",
                        description = "We are seeking a Full-Stack C# .NET Developer with strong MongoDB experience. You will design, build, and maintain robust API servers using ASP.NET Core, integration layers with non-relational database structures, and help integrate companion mobile platforms.",
                        requirements = "• 3+ years experience with C# .NET Core.\n• Strong databases skills: MongoDB (NoSQL) & document modeling.\n• Solid understanding of RESTful API design.\n• Experience with containerization (Docker, Kubernetes).\n• Nice to have: Knowledge of Android/iOS app API integrations.",
                        postedTime = "2 hours ago"
                    ),
                    JobPost(
                        id = "2",
                        title = "UI/UX Product Designer",
                        company = "CreativeFlow Agency",
                        location = "New York, NY",
                        salary = "$95k - $110k",
                        type = "Full-time",
                        category = "Design",
                        description = "CreativeFlow is looking for a UI/UX Designer to craft premium, modern, high-contrast visual designs for our portfolio clients. You will build interface designs, layouts, visual prototypes, and work closely with Android/Web engineering groups.",
                        requirements = "• Robust portfolio showcasing mobile and desktop interface layouts.\n• Advanced proficiency in Figma and design tools.\n• Strong grasp of Material 3 and adaptive layout principles.\n• Excellent communication skills and micro-interaction planning.",
                        postedTime = "1 day ago"
                    ),
                    JobPost(
                        id = "3",
                        title = "Senior .NET Enterprise Architect",
                        company = "Enterprise Cloud Corp",
                        location = "Austin, TX (Hybrid)",
                        salary = "$140k - $165k",
                        type = "Full-time",
                        category = "Technology",
                        description = "Enterprise Cloud Corp is scaling its mission-critical infrastructure. We need a seasoned Architect/Engineer to design high-throughput distributed architectures on the Microsoft web platform, leveraging MongoDB and Redis caches.",
                        requirements = "• 7+ years of expertise in C# and advanced .NET framework.\n• Deep knowledge of distributed systems, CQRS, and Event-Driven architecture.\n• Extensive practical integration of MongoDB collections and indexing.\n• Familiarity with Azure and public cloud architectures.",
                        postedTime = "3 days ago"
                    ),
                    JobPost(
                        id = "4",
                        title = "Growth Marketing Manager",
                        company = "BrandAmplify",
                        location = "Los Angeles, CA",
                        salary = "$80k - $95k",
                        type = "Contract",
                        category = "Marketing",
                        description = "BrandAmplify is hiring a creative, data-driven Growth Marketer to lead campaign strategies and scaling models. You will design user aquisition experiments, track metrics, and run content engines across digital visual feeds.",
                        requirements = "• 4+ years running B2C or App Growth funnels.\n• Certified analytics tracking (GA4, Mixpanel, Amplitude).\n• Proven budget scaling and customer acquisition cost (CAC) optimization.\n• Energetic, creative visual layout planner.",
                        postedTime = "4 days ago"
                    ),
                    JobPost(
                        id = "5",
                        title = "Senior iOS & Android Engineer",
                        company = "AppMasters Co",
                        location = "San Francisco, CA (Remote)",
                        salary = "$135k - $155k",
                        type = "Remote",
                        category = "Technology",
                        description = "AppMasters is seeking a Mobile developer to build modern client apps in Jetpack Compose and native Kotlin. You'll sync app data perfectly with background REST endpoints and local SQLite Room database layers.",
                        requirements = "• 4+ years of native mobile development.\n• Heavy Compose UI and offline-first design (Room/SQLite).\n• Experience with modern reactive patterns (StateFlow, Coroutines).\n• Excellent eye for UI motion, spacing, and transition styling.",
                        postedTime = "5 days ago"
                    ),
                    JobPost(
                        id = "6",
                        title = "Lead pediatric Registered Nurse (RN)",
                        company = "St. Jude Care Center",
                        location = "Denver, CO",
                        salary = "$85k - $100k",
                        type = "Full-time",
                        category = "Healthcare",
                        description = "St. Jude Care Center has an opening for a highly dedicated Lead Pediatric Nurse. You will manage outpatient clinical visits, design health care schedules, and lead a stellar group of clinical practitioners.",
                        requirements = "• Active Registered Nurse (RN) License in CO.\n• 3+ years experience in pediatrics or family medicine.\n• BLS (Basic Life Support) and PALS certification.\n• Compassionate client care style.",
                        postedTime = "1 week ago"
                    ),
                    JobPost(
                        id = "7",
                        title = "Business Analyst / Scrum Master",
                        company = "Capital Growth Partners",
                        location = "Chicago, IL",
                        salary = "$90k - $110k",
                        type = "Full-time",
                        category = "Business",
                        description = "Join Capital Growth as a versatile Scrum Master and Financial Business Analyst. You will translate stakeholder user requirements into developmental epics and design visual analytics reports.",
                        requirements = "• Certified Scrum Master (CSM) or equivalent.\n• Strong financial indexing and SQL querying skills.\n• Ability to run sprint plan sessions across multi-lingual teams.\n• Experience with JIRA and product dashboarding assets.",
                        postedTime = "1 week ago"
                    )
                )
                jobDao.insertJobs(defaultJobs)
            }

            // Populate profile if empty
            val defaultProfile = UserProfile(
                id = 1,
                fullName = "Alex Mercer",
                headline = "Senior Software Developer | C# .NET | MongoDB Specialist",
                email = "alex.mercer@gmail.com",
                phone = "+1 (555) 019-2834",
                bio = "Passionate developer specialized in building scalable backend services in C# .NET (ASP.NET Core) backed by document-oriented database engines standard like MongoDB. Experienced in creating native Kotlin companion apps utilizing high-contrast Material 3 UI.",
                skills = "C# .NET, MongoDB, Kotlin, Jetpack Compose, ASP.NET Core REST APIs, Room, Clean Architecture, Git, Docker",
                education = "B.S. in Computer Science - State University (2018 - 2022)",
                experience = "Senior Back-end Developer at CloudVantage (2022 - Present)\n- Engineered robust C# ASP.NET Core microservices handling 2M+ daily network requests.\n- Optimized MongoDB indexing pipelines improving report generation speeds by 40%.\n- Authored offline Room database caching layer for native Android companions."
            )
            profileDao.insertProfile(defaultProfile)
        }
    }
}
