package com.example.intellipath.data

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import io.github.jan.supabase.postgrest.query.Order

@Serializable
data class NewStudentAssessment(
    val student_id: String,
    val assessment_id: String,
    val score: Int,
    val percentage: Int,
    val status: String = "Completed"
)

@Serializable
data class StudentAssessmentRow(
    val student_assessment_id: String,
    val student_id: String? = null,
    val assessment_id: String? = null,
    val score: Int? = null,
    val percentage: Int? = null,
    val status: String? = null
)

@Serializable
data class NewAssessmentResult(
    val student_assessment_id: String,
    val readiness_score: Int,
    val strengths: String? = null,
    val areas_for_improvement: String? = null
)

// NEW: Used when reading the latest completed baseline result
@Serializable
data class StudentAssessmentReadRow(
    val student_assessment_id: String,
    val percentage: Double? = null,
    val status: String? = null
)

object AssessmentRepository {

    interface Callback<T> {
        fun onSuccess(result: T)
        fun onError(message: String)
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    // student_assessments and assessment_results are separate inserts
    // (no single-call nested insert here), so this saves in two steps:
    // first the attempt row (to get student_assessment_id), then the
    // result row that references it.
    @JvmStatic
    fun saveBaselineResult(
        assessmentId: String,
        correctCount: Int,
        percentage: Int,
        strengths: String,
        areasImprovement: String,
        callback: Callback<Unit>
    ) {
        scope.launch {
            try {
                val studentId = LabSimulationRepository.getCurrentStudentId()
                    ?: throw Exception("No authenticated user found")

                val attempt = SupabaseProvider.client.postgrest["student_assessments"]
                    .insert(
                        NewStudentAssessment(
                            student_id = studentId,
                            assessment_id = assessmentId,
                            score = correctCount,
                            percentage = percentage
                        )
                    ) { select() }
                    .decodeSingle<StudentAssessmentRow>()

                SupabaseProvider.client.postgrest["assessment_results"].insert(
                    NewAssessmentResult(
                        student_assessment_id = attempt.student_assessment_id,
                        readiness_score = percentage,
                        strengths = strengths,
                        areas_for_improvement = areasImprovement
                    )
                )

                withContext(Dispatchers.Main) {
                    callback.onSuccess(Unit)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(e.message ?: "Failed to save baseline result")
                }
            }
        }
    }

    // NEW: Fetch the student's latest completed baseline result
    @JvmStatic
    fun fetchLatestBaselineResult(
        assessmentId: String,
        callback: Callback<StudentAssessmentReadRow?>
    ) {
        scope.launch {
            try {
                val studentId = LabSimulationRepository.getCurrentStudentId()
                    ?: throw Exception("No authenticated user found")

                val result = SupabaseProvider.client.postgrest["student_assessments"]
                    .select {
                        filter {
                            eq("student_id", studentId)
                            eq("assessment_id", assessmentId)
                            eq("status", "Completed")
                        }
                        order("completed_at", Order.DESCENDING)
                        limit(1)
                    }
                    .decodeSingleOrNull<StudentAssessmentReadRow>()

                withContext(Dispatchers.Main) {
                    callback.onSuccess(result)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(
                        e.message ?: "Failed to check baseline status"
                    )
                }
            }
        }
    }
}