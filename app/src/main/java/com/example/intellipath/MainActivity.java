package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    // Dashboard cards
    private CardView cardBaseline;
    private CardView cardRoadmap;
    private CardView cardAssessments;
    private CardView cardSimulation;
    private CardView cardCollaboration;
    private CardView cardAchievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect this Java class to activity_main.xml
        setContentView(R.layout.activity_main);

        // Initialise Dashboard views
        initialiseViews();

        // Set Dashboard interactions
        setupClickListeners();
    }

    private void initialiseViews() {
        cardBaseline = findViewById(R.id.cardBaseline);
        cardRoadmap = findViewById(R.id.cardRoadmap);
        cardAssessments = findViewById(R.id.cardAssessments);
        cardSimulation = findViewById(R.id.cardSimulation);
        cardCollaboration = findViewById(R.id.cardCollaboration);
        cardAchievements = findViewById(R.id.cardAchievements);
    }

    private void setupClickListeners() {

        // Baseline Assessment
        cardBaseline.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    BaselineIntroductionActivity.class
            );
            startActivity(intent);
        });

        // Roadmap - currently locked
        cardRoadmap.setOnClickListener(view -> {
            Toast.makeText(
                    MainActivity.this,
                    "Complete the Baseline Assessment to unlock your Roadmap.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Assessments - currently locked
        cardAssessments.setOnClickListener(view -> {
            Toast.makeText(
                    MainActivity.this,
                    "Complete the Baseline Assessment to unlock Assessments.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Simulation - currently locked
        cardSimulation.setOnClickListener(view -> {
            Toast.makeText(
                    MainActivity.this,
                    "Complete the Baseline Assessment to unlock Simulation.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Collaboration - currently locked
        cardCollaboration.setOnClickListener(view -> {
            Toast.makeText(
                    MainActivity.this,
                    "Complete the Baseline Assessment to unlock Collaboration.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Achievements - currently locked
        cardAchievements.setOnClickListener(view -> {
            Toast.makeText(
                    MainActivity.this,
                    "Complete the Baseline Assessment to unlock Achievements.",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}