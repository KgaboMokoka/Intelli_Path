package com.example.intellipath.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Same shape as SupabaseAuthRepository: a singleton object, @JvmStatic
// functions for Java callers, each one hopping back to Dispatchers.Main
// before touching the callback.
object LabSimulationRepository {

    // Same lookup SupabaseAuthRepository does inline in signIn() /
    // updateStudentProfile() — pulled out here so Java callers (the
    // Activity) don't need to touch the Supabase Kotlin API directly.
    @JvmStatic
    fun getCurrentStudentId(): String? =
        SupabaseProvider.client.auth.currentUserOrNull()?.id

    // Single generic callback rather than one interface per operation
    // (AuthCallback / LoginCallback) — every call here is a plain
    // "give me a T or an error", so this avoids five near-identical
    // interfaces. Happy to split these out to match exactly if the team
    // prefers consistency over the extra boilerplate.
    interface Callback<T> {
        fun onSuccess(result: T)
        fun onError(message: String)
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    @JvmStatic
    fun fetchSteps(labId: String, callback: Callback<List<LabStep>>) {
        scope.launch {
            try {
                val steps = SupabaseProvider.client.postgrest["lab_steps"]
                    .select {
                        filter { eq("lab_id", labId) }
                        order("order_index", Order.ASCENDING)
                    }
                    .decodeList<LabStep>()

                withContext(Dispatchers.Main) { callback.onSuccess(steps) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to load steps") }
            }
        }
    }

    @JvmStatic
    fun fetchOptions(stepId: String, callback: Callback<List<LabStepOption>>) {
        scope.launch {
            try {
                val options = SupabaseProvider.client.postgrest["lab_step_options"]
                    .select {
                        filter { eq("step_id", stepId) }
                        order("order_index", Order.ASCENDING)
                    }
                    .decodeList<LabStepOption>()

                withContext(Dispatchers.Main) { callback.onSuccess(options) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to load options") }
            }
        }
    }

    @JvmStatic
    fun startAttempt(studentId: String, labId: String, callback: Callback<StudentLabAttempt>) {
        scope.launch {
            try {
                val attempt = SupabaseProvider.client.postgrest["student_lab_attempts"]
                    .insert(NewLabAttempt(student_id = studentId, lab_id = labId)) { select() }
                    .decodeSingle<StudentLabAttempt>()

                withContext(Dispatchers.Main) { callback.onSuccess(attempt) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to start attempt") }
            }
        }
    }

    @JvmStatic
    fun saveResponse(
        attemptId: String,
        stepId: String,
        selectedOptionId: String?,
        justification: String?,
        callback: Callback<Unit>
    ) {
        scope.launch {
            try {
                SupabaseProvider.client.postgrest["student_lab_step_responses"].insert(
                    NewStepResponse(
                        attempt_id = attemptId,
                        step_id = stepId,
                        selected_option_id = selectedOptionId,
                        justification = justification
                    )
                )
                withContext(Dispatchers.Main) { callback.onSuccess(Unit) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to save response") }
            }
        }
    }

    /**
     * Content convention (fixed by design, not derived at runtime):
     * step order_index 0 = architecture pattern, 2 = database approach,
     * 3 = communication method, 4 = diagram selection. Their option
     * `value`s are what gets matched against lab_rubric_rules.
     */
    @JvmStatic
    fun finalizeAttempt(
        attemptId: String,
        labId: String,
        architecturePatternValue: String,
        databaseApproachValue: String,
        communicationMethodValue: String,
        selectedDiagramValue: String,
        callback: Callback<NewLabResult>
    ) {
        scope.launch {
            try {
                val matchedRule = SupabaseProvider.client.postgrest["lab_rubric_rules"]
                    .select {
                        filter {
                            eq("lab_id", labId)
                            eq("architecture_pattern", architecturePatternValue)
                            eq("database_approach", databaseApproachValue)
                            eq("communication_method", communicationMethodValue)
                        }
                    }
                    .decodeSingleOrNull<LabRubricRule>()

                val scoreBand = when {
                    matchedRule == null -> "Partial - Trade-off Missed"
                    selectedDiagramValue != matchedRule.correct_diagram_value -> "Partial - Trade-off Missed"
                    else -> matchedRule.score_band
                }

                val result = NewLabResult(
                    attempt_id = attemptId,
                    matched_rubric_id = matchedRule?.rubric_id,
                    score_band = scoreBand,
                    feedback = matchedRule?.feedback
                )

                SupabaseProvider.client.postgrest["student_lab_results"].insert(result)

                SupabaseProvider.client.postgrest["student_lab_attempts"].update({
                    set("status", "Completed")
                }) {
                    filter { eq("attempt_id", attemptId) }
                }

                withContext(Dispatchers.Main) { callback.onSuccess(result) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to finalize attempt") }
            }
        }
    }
}