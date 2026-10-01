package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackReports extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_reports);

        // Dashboard
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            startActivity(new Intent(
                    FeedbackReports.this,
                    AdminDash.class
            ));
        });

        // Students
        findViewById(R.id.navStudents).setOnClickListener(v -> {
            startActivity(new Intent(
                    FeedbackReports.this,
                    StudentManagement.class
            ));
        });

        // Assessments
        findViewById(R.id.navAssessments).setOnClickListener(v -> {
            startActivity(new Intent(
                    FeedbackReports.this,
                    AssesmentManagement.class
            ));
        });

        // Reports
        findViewById(R.id.navReports).setOnClickListener(v -> {
            // Already on Reports
        });

        // Settings
        findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(
                    FeedbackReports.this,
                    AdminSettings.class
            ));
        });
    }
}