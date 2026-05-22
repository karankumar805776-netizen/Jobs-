package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs")
    fun getAllJobsFlow(): Flow<List<JobPost>>

    @Query("SELECT * FROM jobs WHERE id = :id")
    fun getJobByIdFlow(id: String): Flow<JobPost?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertJobs(jobs: List<JobPost>)

    @Update
    suspend fun updateJob(job: JobPost)

    @Query("UPDATE jobs SET isSaved = :isSaved WHERE id = :jobId")
    suspend fun updateSaveStatus(jobId: String, isSaved: Boolean)

    @Query("UPDATE jobs SET isApplied = :isApplied, applicationStatus = :status, appliedDate = :appliedDate WHERE id = :jobId")
    suspend fun updateApplicationStatus(jobId: String, isApplied: Boolean, status: String, appliedDate: Long)

    @Query("SELECT COUNT(*) FROM jobs")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleJob(job: JobPost)

    @Query("DELETE FROM jobs WHERE id = :jobId")
    suspend fun deleteJobById(jobId: String)

    @Query("DELETE FROM jobs")
    suspend fun deleteAllJobs()
}
