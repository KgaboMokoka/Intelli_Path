package com.example.intellipath.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object AchievementRepository {

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Serializable
    data class StudentBadgeRow(
        val student_badge_id: String = "",
        val student_id: String? = null,
        val badge_id: String? = null,
        val awarded_at: String? = null
    )

    @Serializable
    data class BadgeRow(
        val badge_id: String = "",
        val name: String = "",
        val description: String? = null,
        val icon_url: String? = null,
        val created_at: String? = null
    )

    @Serializable
    data class StudentAssessmentRow(
        val student_assessment_id: String = "",
        val student_id: String? = null,
        val assessment_id: String? = null,
        val status: String = "",
        val completed_at: String? = null
    )

    @Serializable
    data class AssessmentRow(
        val assessment_id: String = "",
        val title: String = ""
    )

    @Serializable
    data class AssessmentResultRow(
        val result_id: String = "",
        val student_assessment_id: String? = null,
        val strengths: String? = null,
        val areas_for_improvement: String? = null,
        val completed_date: String? = null
    )

    data class EarnedBadge(
        val name: String,
        val description: String?,
        val iconUrl: String?,
        val awardedAt: String?
    )

    data class AssessmentFeedback(
        val assessmentTitle: String,
        val strengths: String?,
        val areasForImprovement: String?,
        val completedAt: String?
    )

    interface Callback {
        fun onSuccess(
            badges: List<EarnedBadge>,
            feedback: List<AssessmentFeedback>
        )

        fun onError(message: String)
    }

    @JvmStatic
    fun loadAchievements(callback: Callback) {

        scope.launch {
            try {
                val client = SupabaseProvider.client

                val studentId =
                    client.auth.currentUserOrNull()?.id
                        ?: throw Exception(
                            "Please log in to view your achievements."
                        )

                // 1. Fetch badges actually awarded to this student.
                val studentBadges =
                    client.postgrest["student_badges"]
                        .select {
                            filter {
                                eq("student_id", studentId)
                            }
                        }
                        .decodeList<StudentBadgeRow>()

                val earnedBadges = mutableListOf<EarnedBadge>()

                for (studentBadge in studentBadges) {
                    val badgeId = studentBadge.badge_id ?: continue

                    val badge =
                        client.postgrest["badges"]
                            .select {
                                filter {
                                    eq("badge_id", badgeId)
                                }
                            }
                            .decodeList<BadgeRow>()
                            .firstOrNull()

                    if (badge != null) {
                        earnedBadges.add(
                            EarnedBadge(
                                name = badge.name,
                                description = badge.description,
                                iconUrl = badge.icon_url,
                                awardedAt = studentBadge.awarded_at
                            )
                        )
                    }
                }

                // 2. Fetch this student's completed assessments.
                val studentAssessments =
                    client.postgrest["student_assessments"]
                        .select {
                            filter {
                                eq("student_id", studentId)
                                eq("status", "Completed")
                            }
                        }
                        .decodeList<StudentAssessmentRow>()

                val feedbackItems =
                    mutableListOf<AssessmentFeedback>()

                for (attempt in studentAssessments) {
                    val attemptId = attempt.student_assessment_id

                    val result =
                        client.postgrest["assessment_results"]
                            .select {
                                filter {
                                    eq(
                                        "student_assessment_id",
                                        attemptId
                                    )
                                }
                            }
                            .decodeList<AssessmentResultRow>()
                            .firstOrNull()
                            ?: continue

                    val assessmentId = attempt.assessment_id

                    val assessmentTitle =
                        if (assessmentId != null) {
                            client.postgrest["assessments"]
                                .select {
                                    filter {
                                        eq("assessment_id", assessmentId)
                                    }
                                }
                                .decodeList<AssessmentRow>()
                                .firstOrNull()
                                ?.title
                        } else {
                            null
                        } ?: "Completed Assessment"

                    feedbackItems.add(
                        AssessmentFeedback(
                            assessmentTitle = assessmentTitle,
                            strengths = result.strengths,
                            areasForImprovement =
                                result.areas_for_improvement,
                            completedAt =
                                attempt.completed_at
                                    ?: result.completed_date
                        )
                    )
                }

                val sortedBadges =
                    earnedBadges.sortedByDescending {
                        it.awardedAt ?: ""
                    }

                val sortedFeedback =
                    feedbackItems.sortedByDescending {
                        it.completedAt ?: ""
                    }

                withContext(Dispatchers.Main) {
                    callback.onSuccess(
                        sortedBadges,
                        sortedFeedback
                    )
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(
                        e.message
                            ?: "Unable to load achievements."
                    )
                }
            }
        }
    }
}