package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobPost(
    @PrimaryKey val id: String,
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val type: String, // "Full-time", "Part-time", "Remote", "Contract"
    val category: String, // "Technology", "Design", "Marketing", "Business", "Healthcare"
    val description: String,
    val requirements: String, // Newline separated
    val postedTime: String,
    val isSaved: Boolean = false,
    val isApplied: Boolean = false,
    val applicationStatus: String? = null, // "Applied", "Reviewing", "Interview", "Offer"
    val appliedDate: Long? = null
)
