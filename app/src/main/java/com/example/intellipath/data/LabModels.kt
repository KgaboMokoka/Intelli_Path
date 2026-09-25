package com.example.intellipath.data

import kotlinx.serialization.Serializable

// Property names match table columns directly (snake_case), same
// convention as NewStudent / StudentRow in SupabaseAuthRepository.kt —
// no @SerialName needed.

@Serializable
data class LabStep(
    val step_id: String,
    val lab_id: String,
    val order_index: Int,
    val step_type: String, // "single_select" | "multi_select" | "image_select"
    val prompt: String,
    val requires_justification: Boolean
)

@Serializable
data class LabStepOption(
    val option_id: String,
    val step_id: String,
    val label: String,
    val value: String,
    val image_url: String? = null,
    val order_index: Int
)

@Serializable
data class NewLabAttempt(
    val student_id: String,
    val lab_id: String,
    val status: String = "In Progress"
)

@Serializable
data class StudentLabAttempt(
    val attempt_id: String,
    val student_id: String,
    val lab_id: String,
    val status: String
)

@Serializable
data class NewStepResponse(
    val attempt_id: String,
    val step_id: String,
    val selected_option_id: String? = null,
    val justification: String? = null
)

@Serializable
data class LabRubricRule(
    val rubric_id: String,
    val lab_id: String,
    val architecture_pattern: String,
    val database_approach: String,
    val communication_method: String,
    val correct_diagram_value: String,
    val score_band: String,
    val feedback: String? = null
)

@Serializable
data class NewLabResult(
    val attempt_id: String,
    val matched_rubric_id: String? = null,
    val score_band: String? = null,
    val feedback: String? = null
)