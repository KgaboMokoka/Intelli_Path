package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intellipath.data.StudentRow;
import com.example.intellipath.data.SupabaseAuthRepository;

public class GeneratedRoadmapActivity extends AppCompatActivity {

    private static final String TAG = "GeneratedRoadmap";

    public static final String EXTRA_PERCENTAGE = "percentage";
    private static final int UNKNOWN = -1;

    // Level thresholds: below 50 = Beginner, 50-74 = Intermediate, 75+ = Advanced
    private static final int INTERMEDIATE_MIN = 50;
    private static final int ADVANCED_MIN = 75;

    private TextView tvCareerGoal;
    private Button btnStartRoadmap;
    private Button btnBackToDashboard;

    private int percentage = UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_roadmap);

        tvCareerGoal = findViewById(R.id.tvCareerGoal);
        btnStartRoadmap = findViewById(R.id.btnStartRoadmap);
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);

        tvCareerGoal.setText("Loading...");

        btnBackToDashboard.setOnClickListener(v -> finish());
        btnStartRoadmap.setOnClickListener(v -> openRoadmapForLevel());

        // Start is disabled until we know which roadmap to open.
//        btnStartRoadmap.setEnabled(false);

        percentage = getIntent().getIntExtra(EXTRA_PERCENTAGE, UNKNOWN);

        percentage = getIntent().getIntExtra(EXTRA_PERCENTAGE, UNKNOWN);

        if (percentage == UNKNOWN) {
            // Opened without a score. The dashboard is the source of truth,
            // so send the student back there instead of guessing a level.
            Toast.makeText(
                    this,
                    "Open your roadmap from the dashboard once your baseline is complete.",
                    Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }

        loadStudentProfile();
    }

    // ============================================================
    // PICK THE ROADMAP
    // ============================================================

    private Class<?> roadmapClassFor(int score) {
        if (score < INTERMEDIATE_MIN) {
            return RoadmapBeginner.class;
        } else if (score < ADVANCED_MIN) {
            return RoadmapIntermediate.class;
        } else {
            return RoadmapAdvanced.class;
        }
    }

    private void openRoadmapForLevel() {
        if (percentage == UNKNOWN) {
            Toast.makeText(
                    this,
                    "Your score is still loading. Please try again.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        startActivity(new Intent(this, roadmapClassFor(percentage)));
    }

    // ============================================================
    // LOAD CAREER GOAL
    // ============================================================

    private void loadStudentProfile() {

        SupabaseAuthRepository.getStudentProfile(
                new SupabaseAuthRepository.StudentProfileCallback() {

                    @Override
                    public void onSuccess(StudentRow profile) {

                        if (isFinishing() || isDestroyed()) return;

                        String careerGoal = profile.getCareer_goal();

                        if (careerGoal == null || careerGoal.trim().isEmpty()) {
                            tvCareerGoal.setText("Career goal not set");
                        } else {
                            tvCareerGoal.setText(careerGoal);
                        }
                    }

                    @Override
                    public void onError(@NonNull String message) {

                        if (isFinishing() || isDestroyed()) return;

                        Log.e(TAG, "Could not load profile: " + message);
                        tvCareerGoal.setText("Unable to load career goal");
                    }
                }
        );
    }
}