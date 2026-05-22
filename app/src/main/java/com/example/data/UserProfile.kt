package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String,
    val email: String,
    val phone: String,
    val headline: String,
    val bio: String,
    val skills: String, // Comma-separated
    val education: String,
    val experience: String
)
