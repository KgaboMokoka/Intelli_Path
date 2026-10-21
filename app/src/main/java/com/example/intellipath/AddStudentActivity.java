
package com.example.intellipath;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddStudentActivity extends AppCompatActivity {

    private EditText inputName, inputStudentId, inputEmail;
    private Spinner spinnerYear, spinnerCampus, spinnerCareer, spinnerStatus;

    private static final String PREFS_NAME = "IntelliPathStudents";
    private static final String STUDENTS_KEY = "students";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        inputName = findViewById(R.id.inputStudentName);
        inputStudentId = findViewById(R.id.inputStudentId);
        inputEmail = findViewById(R.id.inputStudentEmail);

        spinnerYear = findViewById(R.id.spinnerAcademicYear);
        spinnerCampus = findViewById(R.id.spinnerCampus);
        spinnerCareer = findViewById(R.id.spinnerCareerGoal);
        spinnerStatus = findViewById(R.id.spinnerStudentStatus);

        setupSpinner(spinnerYear, new String[]{
                "1st Year", "2nd Year", "3rd Year", "4th Year", "Postgraduate"
        });

        setupSpinner(spinnerCampus, new String[]{
                "Midrand", "Braamfontein", "Sandton", "Other"
        });

        setupSpinner(spinnerCareer, new String[]{
                "Software Engineer", "Backend Developer",
                "Frontend Developer", "Data Analyst",
                "Cybersecurity Analyst", "Product Manager", "Other"
        });

        setupSpinner(spinnerStatus, new String[]{
                "Active", "At Risk", "Inactive"
        });

        findViewById(R.id.btnBackStudents).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancelStudent).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveStudent).setOnClickListener(v -> saveStudent());
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    private void saveStudent() {
        String name = inputName.getText().toString().trim();
        String studentId = inputStudentId.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();

        if (name.isEmpty()) {
            inputName.setError("Enter the student's full name");
            inputName.requestFocus();
            return;
        }

        if (studentId.isEmpty()) {
            inputStudentId.setError("Enter a student ID");
            inputStudentId.requestFocus();
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Enter a valid email address");
            inputEmail.requestFocus();
            return;
        }

        SharedPreferences preferences = getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        );

        try {
            JSONArray students = new JSONArray(
                    preferences.getString(STUDENTS_KEY, "[]")
            );

            for (int i = 0; i < students.length(); i++) {
                JSONObject existing = students.getJSONObject(i);

                if (existing.optString("studentId").equalsIgnoreCase(studentId)) {
                    inputStudentId.setError("This student ID already exists");
                    inputStudentId.requestFocus();
                    return;
                }

                if (existing.optString("email").equalsIgnoreCase(email)) {
                    inputEmail.setError("This email is already registered");
                    inputEmail.requestFocus();
                    return;
                }
            }

            JSONObject student = new JSONObject();
            student.put("name", name);
            student.put("studentId", studentId);
            student.put("email", email);
            student.put("academicYear", spinnerYear.getSelectedItem().toString());
            student.put("campus", spinnerCampus.getSelectedItem().toString());
            student.put("careerGoal", spinnerCareer.getSelectedItem().toString());
            student.put("status", spinnerStatus.getSelectedItem().toString());
            student.put("readiness", 0);

            students.put(student);

            preferences.edit()
                    .putString(STUDENTS_KEY, students.toString())
                    .apply();

            Toast.makeText(
                    this,
                    "Student added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            setResult(RESULT_OK);
            finish();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Could not save student. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
