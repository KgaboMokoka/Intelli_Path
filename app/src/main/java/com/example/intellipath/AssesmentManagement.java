
package com.example.intellipath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AssesmentManagement extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assesment_management);

        preferences = getSharedPreferences("IntelliPathRoadmaps", MODE_PRIVATE);

        findViewById(R.id.btnCreateAssessment).setOnClickListener(v ->
                startActivity(new Intent(this, CreateAssessmentActivity.class)));

        findViewById(R.id.btnNewAssessment).setOnClickListener(v ->
                startActivity(new Intent(this, CreateAssessmentActivity.class)));

        findViewById(R.id.btnEditBeginnerRoadmap).setOnClickListener(v ->
                openRoadmapEditor("beginner"));

        findViewById(R.id.btnEditIntermediateRoadmap).setOnClickListener(v ->
                openRoadmapEditor("intermediate"));

        findViewById(R.id.btnEditAdvancedRoadmap).setOnClickListener(v ->
                openRoadmapEditor("advanced"));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.navDashboard).setOnClickListener(v ->
                startActivity(new Intent(this, AdminDash.class)));

        findViewById(R.id.navStudents).setOnClickListener(v ->
                startActivity(new Intent(this, StudentManagement.class)));

        findViewById(R.id.navAssessments).setOnClickListener(v -> {});

        findViewById(R.id.navReports).setOnClickListener(v ->
                startActivity(new Intent(this, FeedbackReports.class)));

        findViewById(R.id.navSettings).setOnClickListener(v ->
                startActivity(new Intent(this, AdminSettings.class)));

        loadRoadmapCards();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoadmapCards();
    }

    private void openRoadmapEditor(String roadmapKey) {
        Intent intent = new Intent(this, RoadmapEditActivity.class);
        intent.putExtra("roadmapKey", roadmapKey);
        startActivity(intent);
    }

    private void loadRoadmapCards() {
        updateRoadmapCard(
                R.id.txtBeginnerRoadmapTitle,
                R.id.txtBeginnerRoadmapDescription,
                R.id.txtBeginnerRoadmapStats,
                "beginner",
                "Beginner Roadmap",
                "Foundation Backend Developer",
                "6 MODULES     4 LABS       LEVEL: Beginner     0–49%"
        );

        updateRoadmapCard(
                R.id.txtIntermediateRoadmapTitle,
                R.id.txtIntermediateRoadmapDescription,
                R.id.txtIntermediateRoadmapStats,
                "intermediate",
                "Intermediate Roadmap",
                "Backend Developer",
                "8 MODULES     5 LABS       LEVEL: Intermediate     50–74%"
        );

        updateRoadmapCard(
                R.id.txtAdvancedRoadmapTitle,
                R.id.txtAdvancedRoadmapDescription,
                R.id.txtAdvancedRoadmapStats,
                "advanced",
                "Advanced Roadmap",
                "Advanced Backend Developer",
                "10 MODULES     6 LABS       LEVEL: Advanced     75–100%"
        );
    }

    private void updateRoadmapCard(
            int titleId,
            int descriptionId,
            int statsId,
            String key,
            String defaultTitle,
            String defaultDescription,
            String defaultStats) {

        TextView titleView = findViewById(titleId);
        TextView descriptionView = findViewById(descriptionId);
        TextView statsView = findViewById(statsId);

        String title = preferences.getString(key + "_title", defaultTitle);
        String description = preferences.getString(
                key + "_description", defaultDescription);
        String modules = preferences.getString(
                key + "_modules", getDefaultModules(key));
        String labs = preferences.getString(
                key + "_labs", getDefaultLabs(key));
        String level = preferences.getString(
                key + "_level", getDefaultLevel(key));
        String scoreRange = preferences.getString(
                key + "_scoreRange", getDefaultScoreRange(key));

        titleView.setText(title);
        descriptionView.setText(description);
        statsView.setText(modules + " MODULES     " + labs
                + " LABS       LEVEL: " + level + "     " + scoreRange);
    }

    private String getDefaultModules(String key) {
        switch (key) {
            case "beginner":
                return "6";
            case "intermediate":
                return "8";
            default:
                return "10";
        }
    }

    private String getDefaultLabs(String key) {
        switch (key) {
            case "beginner":
                return "4";
            case "intermediate":
                return "5";
            default:
                return "6";
        }
    }

    private String getDefaultLevel(String key) {
        return key.substring(0, 1).toUpperCase() + key.substring(1);
    }

    private String getDefaultScoreRange(String key) {
        switch (key) {
            case "beginner":
                return "0–49%";
            case "intermediate":
                return "50–74%";
            default:
                return "75–100%";
        }
    }
}
