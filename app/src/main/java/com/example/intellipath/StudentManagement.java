package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class StudentManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);

        // Dashboard
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            startActivity(new Intent(
                    StudentManagement.this,
                    AdminDash.class
            ));
        });

        // Students
        findViewById(R.id.navStudents).setOnClickListener(v -> {
            // Already on Students
        });

        // Assessments
        findViewById(R.id.navAssessments).setOnClickListener(v -> {
            startActivity(new Intent(
                    StudentManagement.this,
                    AssesmentManagement.class
            ));
        });

        // Reports
        findViewById(R.id.navReports).setOnClickListener(v -> {
            startActivity(new Intent(
                    StudentManagement.this,
                    FeedbackReports.class
            ));
        });

        // Settings
        findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(
                    StudentManagement.this,
                    AdminSettings.class
            ));
        });
    }
}