package com.example.intellipath.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

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

object SupabaseAuthRepository {
    interface AuthCallback {
        fun onSuccess()
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
                }
                val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                    ?: throw Exception("No user id returned")

                SupabaseProvider.client.postgrest["students"].insert(
                    NewStudent(
                        student_id = userId,
                        first_name = firstName,
                        last_name = lastName,
                        student_number = studentNumber,
                        email = email
                    )
                )

                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Sign up failed") }
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
                    filter {
                        eq("student_id", userId)
                    }
                }

                withContext(Dispatchers.Main) { callback.onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to update profile") }
            }
        }
    }
}