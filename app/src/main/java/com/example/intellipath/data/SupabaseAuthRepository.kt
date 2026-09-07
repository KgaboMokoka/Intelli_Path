package com.example.intellipath.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class NewStudent(
    val student_id: String,
    val first_name: String,
    val last_name: String,
    val student_number: String,
    val email: String
)

@Serializable
data class StudentProfileUpdate(
    val campus: String,
    val current_academic_year: String,
    val career_goal: String
)

@Serializable
data class StudentRow(
    val student_id: String,
    val campus: String? = null,
    val current_academic_year: String? = null,
    val career_goal: String? = null
)

object SupabaseAuthRepository {
    interface AuthCallback {
        fun onSuccess()
        fun onError(message: String)
    }

    interface LoginCallback {
        fun onNeedsRegistration()
        fun onComplete()
        fun onError(message: String)
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    @JvmStatic
    fun signUp(
        firstName: String,
        lastName: String,
        studentNumber: String,
        email: String,
        password: String,
        callback: AuthCallback
    ) {
        scope.launch {
            try {
                SupabaseProvider.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = buildJsonObject {
                        put("first_name", firstName)
                        put("last_name", lastName)
                        put("student_number", studentNumber)
                    }
                }
                // No students row insert here — no session exists yet until the
                // user confirms their email. The row is created on first login instead.
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Sign up failed") }
            }
        }
    }

    @JvmStatic
    fun resendConfirmation(
        email: String,
        callback: AuthCallback
    ) {
        scope.launch {
            try {
                SupabaseProvider.client.auth.resendEmail(
                    type = OtpType.Email.SIGNUP,
                    email = email
                )
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Could not resend email") }
            }
        }
    }

    @JvmStatic
    fun signIn(
        email: String,
        password: String,
        callback: LoginCallback
    ) {
        scope.launch {
            try {
                SupabaseProvider.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                val user = SupabaseProvider.client.auth.currentUserOrNull()
                    ?: throw Exception("Login failed — no session returned")
                val userId = user.id

                val existing = SupabaseProvider.client.postgrest["students"]
                    .select {
                        filter { eq("student_id", userId) }
                    }
                    .decodeSingleOrNull<StudentRow>()

                if (existing == null) {
                    // First login after confirming — create the students row now,
                    // using the metadata captured at sign-up time.
                    val metadata = user.userMetadata
                    val firstName = metadata?.get("first_name")?.jsonPrimitive?.content ?: ""
                    val lastName = metadata?.get("last_name")?.jsonPrimitive?.content ?: ""
                    val studentNumber = metadata?.get("student_number")?.jsonPrimitive?.content ?: ""

                    SupabaseProvider.client.postgrest["students"].insert(
                        NewStudent(
                            student_id = userId,
                            first_name = firstName,
                            last_name = lastName,
                            student_number = studentNumber,
                            email = email
                        )
                    )

                    withContext(Dispatchers.Main) { callback.onNeedsRegistration() }
                    return@launch
                }

                val isComplete = !existing.campus.isNullOrEmpty() &&
                        !existing.current_academic_year.isNullOrEmpty() &&
                        !existing.career_goal.isNullOrEmpty()

                withContext(Dispatchers.Main) {
                    if (isComplete) callback.onComplete() else callback.onNeedsRegistration()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Login failed") }
            }
        }
    }

    @JvmStatic
    fun updateStudentProfile(
        campus: String,
        academicYear: String,
        careerGoal: String,
        callback: AuthCallback
    ) {
        scope.launch {
            try {
                val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                    ?: throw Exception("No authenticated user found")

                SupabaseProvider.client.postgrest["students"].update(
                    StudentProfileUpdate(
                        campus = campus,
                        current_academic_year = academicYear,
                        career_goal = careerGoal
                    )
                ) {
                    filter { eq("student_id", userId) }
                }

                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to update profile") }
            }
        }
    }
}