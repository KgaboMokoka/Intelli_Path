package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AssesmentManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assesment_management);

        // Dashboard
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            startActivity(new Intent(
                    AssesmentManagement.this,
                    AdminDash.class
            ));
        });

        // Students
        findViewById(R.id.navStudents).setOnClickListener(v -> {
            startActivity(new Intent(
                    AssesmentManagement.this,
                    StudentManagement.class
            ));
        });

        // Assessments
        findViewById(R.id.navAssessments).setOnClickListener(v -> {
            // Already on Assessments
        });

        // Reports
        findViewById(R.id.navReports).setOnClickListener(v -> {
            startActivity(new Intent(
                    AssesmentManagement.this,
                    FeedbackReports.class
            ));
        });

        // Settings
        findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(
                    AssesmentManagement.this,
                    AdminSettings.class
            ));
        });
    }
}