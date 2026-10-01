package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AdminSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        // Dashboard
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminSettings.this,
                    AdminDash.class
            ));
        });

        // Students
        findViewById(R.id.navStudents).setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminSettings.this,
                    StudentManagement.class
            ));
        });

        // Assessments
        findViewById(R.id.navAssessments).setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminSettings.this,
                    AssesmentManagement.class
            ));
        });

        // Reports
        findViewById(R.id.navReports).setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminSettings.this,
                    FeedbackReports.class
            ));
        });

        // Settings
        findViewById(R.id.navSettings).setOnClickListener(v -> {
            // Already on Settings
        });
    }
}