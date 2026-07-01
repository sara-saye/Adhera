package com.gpproject.adhera.detection.reports

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

data class DiagnosticReportHistoryItem(
    val id: String = "",
    val reportNumber: Int = 1,
    val timestamp: Long = 0L,
    val finalProbability: Int = 0,
    val modelResults: List<ModelResult> = emptyList()
)

class DetectionReportHistoryRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val currentUid get() = auth.currentUser?.uid

    private fun reportHistoryCollection() =
        currentUid?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .collection("report_history")
        }

    suspend fun saveAdditionalReport(uiState: DetectionResultsUiState): Result<Int> {
        return try {
            val collection = reportHistoryCollection()
                ?: return Result.failure(IllegalStateException("No authenticated user"))

            val historyCount = collection.get().await().size()
            val reportNumber = historyCount + 2

            val payload = hashMapOf(
                "userId" to currentUid.orEmpty(),
                "timestamp" to System.currentTimeMillis(),
                "reportNumber" to reportNumber,
                "finalProbability" to uiState.finalProbability,
                "reportData" to uiState.modelResults.map { result ->
                    mapOf(
                        "title" to result.title,
                        "percentage" to result.percentage,
                        "iconType" to result.iconType.name
                    )
                }
            )

            collection.add(payload).await()
            Result.success(reportNumber)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadReports(
        pageSize: Long = 10,
        after: DocumentSnapshot? = null
    ): Result<Pair<List<DiagnosticReportHistoryItem>, DocumentSnapshot?>> {
        return try {
            val collection = reportHistoryCollection()
                ?: return Result.failure(IllegalStateException("No authenticated user"))

            var query = collection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(pageSize)

            if (after != null) {
                query = query.startAfter(after)
            }

            val snapshot = query.get().await()
            val items = snapshot.documents.mapNotNull { document ->
                val reportData = document.get("reportData") as? List<*>
                val results = reportData.orEmpty().mapNotNull { raw ->
                    val map = raw as? Map<*, *> ?: return@mapNotNull null
                    val iconName = map["iconType"] as? String
                    ModelResult(
                        title = map["title"] as? String ?: return@mapNotNull null,
                        percentage = (map["percentage"] as? Number)?.toInt() ?: 0,
                        iconType = iconName?.let { runCatching { ModelIconType.valueOf(it) }.getOrNull() }
                            ?: ModelIconType.QUESTIONNAIRE
                    )
                }

                DiagnosticReportHistoryItem(
                    id = document.id,
                    reportNumber = document.getLong("reportNumber")?.toInt() ?: 1,
                    timestamp = document.getLong("timestamp") ?: 0L,
                    finalProbability = document.getLong("finalProbability")?.toInt() ?: 0,
                    modelResults = results
                )
            }

            Result.success(items to snapshot.documents.lastOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReport(reportId: String): Result<Unit> {
        return try {
            val collection = reportHistoryCollection()
                ?: return Result.failure(IllegalStateException("No authenticated user"))

            collection.document(reportId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
