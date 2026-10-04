package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash);

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDash.this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Dashboard
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            // Already on the Admin Dashboard
        });

        // Students
        findViewById(R.id.navStudents).setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminDash.this,
                    StudentManagement.class
            ));
        });

        // Assessments
        findViewById(R.id.navAssessments).setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminDash.this,
                    AssesmentManagement.class
            ));
        });

        // Reports
        findViewById(R.id.navReports).setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminDash.this,
                    FeedbackReports.class
            ));
        });

        // Settings
        findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminDash.this,
                    AdminSettings.class
            ));
        });
    }
}