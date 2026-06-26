package com.gpproject.adhera.doctor.data

import kotlinx.coroutines.flow.Flow

class DoctorRepository(
    private val dao: DoctorDao
) {
    fun observePatients(): Flow<List<PatientEntity>> = dao.observePatients()

    fun observePatientsWithResults(): Flow<List<PatientWithResults>> =
        dao.observePatientsWithResults()

    fun searchPatientsWithResults(query: String): Flow<List<PatientWithResults>> =
        dao.searchPatientsWithResults(query)

    suspend fun saveResultForExistingPatient(
        patientId: Long,
        testType: String,
        testResult: String
    ) {
        dao.insertTestResult(
            TestResultEntity(
                patientId = patientId,
                testType = testType,
                testResult = testResult
            )
        )
    }

    suspend fun createPatientAndSaveResult(
        patientName: String,
        testType: String,
        testResult: String
    ) {
        val patientId = dao.insertPatient(PatientEntity(patientName = patientName.trim()))
        saveResultForExistingPatient(patientId, testType, testResult)
    }

    suspend fun deletePatient(patient: PatientEntity) {
        dao.deletePatient(patient)
    }
}
