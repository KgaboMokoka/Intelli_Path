package com.example.intellipath.data

import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Serializable
data class NewStudent(
    val student_id: String,
    val first_name: String,
    val last_name: String,
    val student_number: String? = null,
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
    val first_name: String? = null,
    val last_name: String? = null,
    val student_number: String? = null,
    val email: String? = null,
    val campus: String? = null,
    val current_academic_year: String? = null,
    val career_goal: String? = null
)

object SupabaseAuthRepository {

    interface AuthCallback {
        fun onSuccess()
        fun onError(message: String)
    }

    interface StudentProfileCallback {
        fun onSuccess(student: StudentRow)
        fun onError(message: String)
    }

    interface LoginCallback {
        fun onNeedsRegistration()
        fun onComplete()
        fun onError(message: String)
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    // ============================================================
    // STUDENT PROFILE
    // ============================================================

    @JvmStatic
    fun getCurrentStudentProfile(
        callback: StudentProfileCallback
    ) {
        scope.launch {
            try {
                val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                    ?: throw Exception("No authenticated user found")

                val student = SupabaseProvider.client.postgrest["students"]
                    .select {
                        filter {
                            eq("student_id", userId)
                        }
                    }
                    .decodeSingleOrNull<StudentRow>()

                if (student == null) {
                    throw Exception("Student profile not found")
                }

                withContext(Dispatchers.Main) {
                    callback.onSuccess(student)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(
                        e.message ?: "Failed to load student profile"
                    )
                }
            }
        }
    }

    // Alias for Java interoperability (GeneratedRoadmapActivity)
    @JvmStatic
    fun getStudentProfile(
        callback: StudentProfileCallback
    ) {
        getCurrentStudentProfile(callback)
    }

    /**
     * Makes sure a students row exists for the signed-in user
     * (creating it from the sign-up metadata on first use).
     * Returns true if the profile is already complete.
     */
    private suspend fun ensureStudentProfile(): Boolean {
        val user = SupabaseProvider.client.auth.currentUserOrNull()
            ?: throw Exception("No active session")
        val userId = user.id

        val existing = SupabaseProvider.client.postgrest["students"]
            .select {
                filter { eq("student_id", userId) }
            }
            .decodeSingleOrNull<StudentRow>()

        if (existing == null) {
            val metadata = user.userMetadata
            val firstName = metadata?.get("first_name")?.jsonPrimitive?.contentOrNull ?: ""
            val lastName = metadata?.get("last_name")?.jsonPrimitive?.contentOrNull ?: ""
            val studentNumber = metadata?.get("student_number")?.jsonPrimitive?.contentOrNull
                ?.takeIf { it.isNotBlank() }

            SupabaseProvider.client.postgrest["students"].insert(
                NewStudent(
                    student_id = userId,
                    first_name = firstName,
                    last_name = lastName,
                    student_number = studentNumber,
                    email = user.email ?: ""
                )
            )
            return false
        }

        return !existing.campus.isNullOrEmpty() &&
                !existing.current_academic_year.isNullOrEmpty() &&
                !existing.career_goal.isNullOrEmpty()
    }

    // ============================================================
    // SIGN UP + EMAIL CONFIRMATION (6-digit code)
    // ============================================================

    @JvmStatic
    fun signUp(
        firstName: String,
        lastName: String,
        studentNumber: String?,
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
                        put("student_number", studentNumber?.takeIf { it.isNotBlank() })
                    }
                }
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

    // Confirms the sign-up code, then makes sure the students row exists.
    @JvmStatic
    fun verifySignupCode(
        email: String,
        code: String,
        callback: LoginCallback
    ) {
        scope.launch {
            try {
                SupabaseProvider.client.auth.verifyEmailOtp(
                    type = OtpType.Email.SIGNUP,
                    email = email,
                    token = code
                )

                val isComplete = ensureStudentProfile()

                withContext(Dispatchers.Main) {
                    if (isComplete) callback.onComplete() else callback.onNeedsRegistration()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(e.message ?: "Invalid or expired code")
                }
            }
        }
    }

    // ============================================================
    // SIGN IN / SIGN OUT
    // ============================================================

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

                val isComplete = ensureStudentProfile()

                withContext(Dispatchers.Main) {
                    if (isComplete) callback.onComplete() else callback.onNeedsRegistration()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Login failed") }
            }
        }
    }

    @JvmStatic
    fun signOut(callback: AuthCallback) {
        scope.launch {
            try {
                SupabaseProvider.client.auth.signOut()
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Sign out failed") }
            }
        }
    }

    // ============================================================
    // PASSWORD RESET (6-digit code)
    // ============================================================

    @JvmStatic
    fun sendPasswordReset(
        email: String,
        callback: AuthCallback
    ) {
        scope.launch {
            try {
                SupabaseProvider.client.auth.resetPasswordForEmail(email = email)
                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(e.message ?: "Could not send reset email")
                }
            }
        }
    }

    // Verifies the reset code, sets the new password, then signs out
    // so the student logs in again with it.
    @JvmStatic
    fun resetPasswordWithCode(
        email: String,
        code: String,
        newPassword: String,
        callback: AuthCallback
    ) {
        scope.launch {
            try {
                SupabaseProvider.client.auth.verifyEmailOtp(
                    type = OtpType.Email.RECOVERY,
                    email = email,
                    token = code
                )

                SupabaseProvider.client.auth.updateUser {
                    password = newPassword
                }

                SupabaseProvider.client.auth.signOut()

                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(e.message ?: "Could not reset password")
                }
            }
        }
    }

    // ============================================================
    // REGISTRATION STEP 2
    // ============================================================

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
                withContext(Dispatchers.Main) {
                    callback.onError(
                        e.message ?: "Failed to update profile"
                    )
                }
            }
        }
    }
}