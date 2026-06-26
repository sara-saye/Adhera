package com.gpproject.adhera.doctor.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val patientId: Long = 0,
    val patientName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "test_results",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true)
    val resultId: Long = 0,
    val patientId: Long,
    val testType: String,
    val testResult: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class PatientWithResults(
    @Embedded val patient: PatientEntity,
    @Relation(
        parentColumn = "patientId",
        entityColumn = "patientId"
    )
    val results: List<TestResultEntity>
)
