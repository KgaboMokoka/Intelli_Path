package com.example.intellipath.data

import com.example.intellipath.data.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object AssessmentHistoryRepository {

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ============================================================
    // DATABASE MODELS
    // ============================================================

    @Serializable
    data class StudentAssessmentRow(
        val student_assessment_id: String = "",
        val student_id: String? = null,
        val assessment_id: String? = null,
        val score: Int? = null,
        val percentage: Double? = null,
        val status: String = "",
        val started_at: String? = null,
        val completed_at: String? = null
    )

    @Serializable
    data class AssessmentRow(
        val assessment_id: String = "",
        val title: String = "",
        val description: String? = null,
        val type: String = "",
        val duration_minutes: Int? = null,
        val total_questions: Int? = null
    )

    @Serializable
    data class AssessmentResultRow(
        val result_id: String = "",
        val student_assessment_id: String? = null,
        val readiness_score: Double? = null,
        val strengths: String? = null,
        val areas_for_improvement: String? = null,
        val completed_date: String? = null
    )

    @Serializable
    data class RoadmapRow(
        val roadmap_id: String = "",
        val student_id: String? = null,
        val title: String? = null,
        val description: String? = null
    )

    @Serializable
    data class RoadmapLabRow(
        val roadmap_lab_id: String = "",
        val roadmap_id: String? = null,
        val lab_id: String? = null,
        val recommended_order: Int? = null,
        val status: String = "",
        val completion_date: String? = null
    )

    @Serializable
    data class PracticalLabRow(
        val lab_id: String = "",
        val title: String = "",
        val description: String? = null,
        val language: String? = null,
        val difficulty_level: String = ""
    )


    // ============================================================
    // MODELS RETURNED TO THE ACTIVITY
    // ============================================================

    data class AssessmentHistoryItem(
        val title: String,
        val type: String,
        val score: Int?,
        val percentage: Double?,
        val readinessScore: Double?,
        val strengths: String?,
        val areasForImprovement: String?,
        val completedAt: String?,
        val durationMinutes: Int?,
        val totalQuestions: Int?
    )

    data class CompletedLabHistoryItem(
        val title: String,
        val description: String?,
        val language: String?,
        val difficulty: String,
        val roadmapTitle: String?,
        val completedAt: String?
    )


    // ============================================================
    // CALLBACK
    // ============================================================

    interface Callback {
        fun onSuccess(
            assessments: List<AssessmentHistoryItem>,
            labs: List<CompletedLabHistoryItem>
        )

        fun onError(message: String)
    }


    // ============================================================
    // LOAD THE CURRENT STUDENT'S HISTORY
    // ============================================================

    @JvmStatic
    fun loadHistory(callback: Callback) {

        scope.launch {

            try {

                val client = SupabaseProvider.client

                val studentId =
                    client.auth.currentUserOrNull()?.id
                        ?: throw Exception(
                            "You must be logged in to view your history."
                        )


                // ------------------------------------------------
                // 1. FETCH COMPLETED ASSESSMENTS
                // ------------------------------------------------

                val studentAssessments =
                    client.postgrest["student_assessments"]
                        .select {
                            filter {
                                eq("student_id", studentId)
                                eq("status", "Completed")
                            }
                        }
                        .decodeList<StudentAssessmentRow>()


                val assessmentHistory =
                    mutableListOf<AssessmentHistoryItem>()


                for (attempt in studentAssessments) {

                    val assessmentId =
                        attempt.assessment_id ?: continue

                    // Get the assessment title and metadata.
                    val assessment =
                        client.postgrest["assessments"]
                            .select {
                                filter {
                                    eq("assessment_id", assessmentId)
                                }
                            }
                            .decodeList<AssessmentRow>()
                            .firstOrNull()

                    // If the referenced assessment is unavailable,
                    // skip it rather than inventing a title.
                    if (assessment == null) {
                        continue
                    }

                    // Get the optional detailed result record.
                    val result =
                        client.postgrest["assessment_results"]
                            .select {
                                filter {
                                    eq(
                                        "student_assessment_id",
                                        attempt.student_assessment_id
                                    )
                                }
                            }
                            .decodeList<AssessmentResultRow>()
                            .firstOrNull()


                    assessmentHistory.add(
                        AssessmentHistoryItem(
                            title = assessment.title,
                            type = assessment.type,
                            score = attempt.score,
                            percentage = attempt.percentage,
                            readinessScore = result?.readiness_score,
                            strengths = result?.strengths,
                            areasForImprovement =
                                result?.areas_for_improvement,
                            completedAt =
                                attempt.completed_at
                                    ?: result?.completed_date,
                            durationMinutes =
                                assessment.duration_minutes,
                            totalQuestions =
                                assessment.total_questions
                        )
                    )
                }


                // Most recently completed assessment first.
                val sortedAssessments =
                    assessmentHistory.sortedByDescending {
                        it.completedAt ?: ""
                    }


                // ------------------------------------------------
                // 2. FETCH THE CURRENT STUDENT'S ROADMAPS
                // ------------------------------------------------

                val roadmaps =
                    client.postgrest["roadmaps"]
                        .select {
                            filter {
                                eq("student_id", studentId)
                            }
                        }
                        .decodeList<RoadmapRow>()


                val completedLabs =
                    mutableListOf<CompletedLabHistoryItem>()


                // ------------------------------------------------
                // 3. FETCH COMPLETED LABS FOR EACH ROADMAP
                // ------------------------------------------------

                for (roadmap in roadmaps) {

                    val roadmapLabs =
                        client.postgrest["roadmap_labs"]
                            .select {
                                filter {
                                    eq(
                                        "roadmap_id",
                                        roadmap.roadmap_id
                                    )
                                    eq("status", "Completed")
                                }
                            }
                            .decodeList<RoadmapLabRow>()


                    for (roadmapLab in roadmapLabs) {

                        val labId =
                            roadmapLab.lab_id ?: continue

                        val lab =
                            client.postgrest["practical_labs"]
                                .select {
                                    filter {
                                        eq("lab_id", labId)
                                    }
                                }
                                .decodeList<PracticalLabRow>()
                                .firstOrNull()

                        if (lab == null) {
                            continue
                        }


                        completedLabs.add(
                            CompletedLabHistoryItem(
                                title = lab.title,
                                description = lab.description,
                                language = lab.language,
                                difficulty = lab.difficulty_level,
                                roadmapTitle = roadmap.title,
                                completedAt =
                                    roadmapLab.completion_date
                            )
                        )
                    }
                }


                // Most recently completed lab first.
                val sortedLabs =
                    completedLabs.sortedByDescending {
                        it.completedAt ?: ""
                    }


                // ------------------------------------------------
                // 4. RETURN THE RESULTS ON THE MAIN THREAD
                // ------------------------------------------------

                withContext(Dispatchers.Main) {

                    callback.onSuccess(
                        sortedAssessments,
                        sortedLabs
                    )
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    callback.onError(
                        e.message
                            ?: "Unable to load assessment history."
                    )
                }
            }
        }
    }
}