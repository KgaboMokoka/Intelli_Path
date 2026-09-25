package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intellipath.data.SupabaseAuthRepository;
import com.example.intellipath.data.StudentRow;

import org.jetbrains.annotations.NotNull;

public class GeneratedRoadmapActivity extends AppCompatActivity {

    private TextView tvCareerGoal;
    private Button btnStartRoadmap;
    private Button btnBackToDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_roadmap);

        tvCareerGoal = findViewById(R.id.tvCareerGoal);
        btnStartRoadmap = findViewById(R.id.btnStartRoadmap);
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);

        // Show a temporary loading message while the profile is retrieved.
        tvCareerGoal.setText("Loading...");

        loadStudentProfile();

        btnBackToDashboard.setOnClickListener(v -> {
            finish();
        });

        btnStartRoadmap.setOnClickListener(v -> {
            // Boity edit: Select the roadmap based on assessment score
            double assessmentPercentage = getIntent().getIntExtra("percentage", 0);

            if (assessmentPercentage < 50) {

                startActivity(new Intent(
                        GeneratedRoadmapActivity.this,
                        RoadmapBeginner.class
                ));

            } else if (assessmentPercentage < 75) {

                startActivity(new Intent(
                        GeneratedRoadmapActivity.this,
                        RoadmapIntermediate.class
                ));

            } else {

                startActivity(new Intent(
                        GeneratedRoadmapActivity.this,
                        RoadmapAdvanced.class
                ));
            }
        });
    }
    private void loadStudentProfile() {

        SupabaseAuthRepository.getStudentProfile(
                new SupabaseAuthRepository.StudentProfileCallback() {

                    @Override
                    public void onSuccess(StudentRow profile) {

                        String careerGoal = profile.getCareer_goal();

                        if (careerGoal == null || careerGoal.isEmpty()) {
                            tvCareerGoal.setText("Career goal not set");
                        } else {
                            tvCareerGoal.setText(careerGoal);
                        }
                    }

                    @Override
                    public void onError(@NotNull String message) {

                        tvCareerGoal.setText("Unable to load career goal");

                        Toast.makeText(
                                GeneratedRoadmapActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}