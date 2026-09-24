package com.example.intellipath;

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
            Toast.makeText(
                    GeneratedRoadmapActivity.this,
                    "Roadmap started!",
                    Toast.LENGTH_SHORT
            ).show();
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