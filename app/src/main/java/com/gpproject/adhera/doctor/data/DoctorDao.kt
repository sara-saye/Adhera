package com.gpproject.adhera.doctor.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestResult(result: TestResultEntity): Long

    @Delete
    suspend fun deletePatient(patient: PatientEntity)

    @Query("SELECT * FROM patients ORDER BY patientName COLLATE NOCASE ASC")
    fun observePatients(): Flow<List<PatientEntity>>

    @Transaction
    @Query("SELECT * FROM patients ORDER BY patientName COLLATE NOCASE ASC")
    fun observePatientsWithResults(): Flow<List<PatientWithResults>>

    @Transaction
    @Query("SELECT * FROM patients WHERE patientName LIKE '%' || :query || '%' ORDER BY patientName COLLATE NOCASE ASC")
    fun searchPatientsWithResults(query: String): Flow<List<PatientWithResults>>
}
